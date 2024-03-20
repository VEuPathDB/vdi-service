package vdi.component.pruner

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.AppDatabaseRegistry
import org.veupathdb.vdi.lib.db.app.model.DeleteFlag
import org.veupathdb.vdi.lib.db.app.withTransaction
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DeletedDataset
import vdi.component.db.cache.withTransaction
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import vdi.component.metrics.Metrics
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * Dataset Pruner
 *
 * This object represents and performs the task of pruning old/dead datasets
 * from the VDI system.
 *
 * Datasets are considered "prunable" if they have been marked as soft-deleted
 * for longer than a configured amount of time.  After this point they are
 * subject to the following operations:
 *
 * 1. Deletion from the control tables in all relevant application databases.
 * 2. Deletion from the control tables in the VDI cache database.
 * 3. Deletion of all relevant objects in the S3 instance.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
object Pruner {
  private val log = LoggerFactory.getLogger(javaClass)

  private val lock = ReentrantLock()

  private val config = PrunerConfig()

  private val cacheDB = vdi.component.db.cache.CacheDB()

  private val appDB = AppDB()

  /**
   * Tries to lock the pruner to prune old/dead datasets.
   *
   * If the pruner could not be locked because a pruning job is already in
   * progress, this method will return immediately without doing anything.
   *
   * If the pruner was not already active, this method will lock the pruner and
   * execute the pruning job, only unlocking once the job is completed.
   *
   * @return `true` if the pruning job executed, `false` if the pruner lock
   * could not be acquired due to an already running pruning job.
   */
  fun tryPruneDatasets(): Boolean {
    return if (lock.tryLock()) {
      try {
        doPruning()
      } finally {
        lock.unlock()
      }

      true
    } else {
      false
    }
  }

  /**
   * Tests whether the [Pruner] is currently locked (has a prune job in
   * progress).
   *
   * @return `true` if the Pruner is currently executing a prune job, otherwise
   * `false`.
   */
  fun isLocked() = lock.isLocked

  private fun doPruning() {
    log.info("starting dataset pruning job")

    val s3Client = S3Api.newClient(config.s3Config)
    val s3Bucket = s3Client.buckets[config.bucketName]
      ?: throw IllegalStateException("bucket ${config.bucketName} does not exist!")

    val dsm = DatasetManager(s3Bucket)

    // Get a list of datasets that have been marked as deleted in the internal
    // postgres database, then filter that list down to only those datasets that
    // were deleted long enough ago to be eligible for pruning.
    val deletable = with(cacheDB.selectDeletedDatasets()) {
      val threshold = OffsetDateTime.now().minus(config.pruneAge.inWholeSeconds, ChronoUnit.SECONDS)
      filter { it.wasDeletedLongEnoughAgoToPrune(dsm, threshold) }
    }

    log.info("found {} candidates for pruning", deletable.size)

    deletable.forEach {
      if (!it.hasBeenUninstalled())
        return@forEach

      try {
        runDelete(s3Bucket, it)
        Metrics.Pruner.count.inc()
      } catch (e: Throwable) {
        Metrics.Pruner.failed.inc()
        log.error("failed to delete dataset ${it.ownerID}/${it.datasetID} due to exception:", e)
      }
    }
  }

  /**
   * Iterates through all the dataset's install target app databases to ensure
   * that the dataset was successfully uninstalled from all of them before
   * allowing the dataset to be removed from MinIO.
   *
   * This is necessary to prevent the reconciler from removing the control
   * records for the dataset from target app databases that still contain traces
   * of the dataset.
   *
   * @receiver Record representing the dataset for which the install targets
   * should be tested.
   *
   * @return `true` if the dataset has been successfully uninstalled from all
   * install targets, `false` if one or more install targets still contain
   * traces of the dataset.
   */
  private fun DeletedDataset.hasBeenUninstalled(): Boolean {
    projects.forEach { projectID ->
      val appDB = appDB.accessor(projectID)

      if (appDB == null) {
        log.warn("cannot prune dataset {}/{} as the dataset's install target {} is currently disabled", ownerID, datasetID, projectID)
        return false
      }

      val installRecord = appDB.selectDataset(datasetID)

      if (installRecord == null) {
        log.warn("dataset {}/{} does not have a control record in install target {}, this will not prevent pruning", ownerID, datasetID, projectID)
        return@forEach
      }

      if (installRecord.isDeleted != DeleteFlag.DeletedAndUninstalled) {
        log.warn("cannot prune dataset {}/{} as it has not been successfully uninstalled from target {}", ownerID, datasetID, projectID)
        return false
      }
    }

    return true
  }

  private fun DeletedDataset.wasDeletedLongEnoughAgoToPrune(dsm: DatasetManager, threshold: OffsetDateTime): Boolean {
    val dir = dsm.getDatasetDirectory(ownerID, datasetID)

    // If the directory doesn't exist, then it has already been deleted.
    if (!dir.exists()) {
      Metrics.Pruner.conflict.inc()
      log.error("dataset {}/{} still has a record in the cache db even though it has been deleted from S3", ownerID, datasetID)
      return false
    }

    if (!dir.hasDeleteFlag()) {
      Metrics.Pruner.conflict.inc()
      log.error("dataset {}/{} is marked as deleted in the cache db but has no delete flag in S3", ownerID, datasetID)
      return false
    }

    return dir.getDeleteFlag()
      .lastModified()!!
      .isBefore(threshold)
  }

  private fun runDelete(bucket: S3Bucket, ds: DeletedDataset) {
    log.info("hard-deleting dataset {}/{}", ds.ownerID, ds.datasetID)

    // Delete records from app databases
    ds.projects.forEach { projectID -> ds.deleteFromAppDB(projectID) }

    // Delete records from cache DB
    ds.deleteFromCacheDB()

    // Delete objects from S3
    ds.deleteFromS3(bucket)
  }

  private fun DeletedDataset.deleteFromAppDB(projectID: ProjectID) {
    if (projectID !in AppDatabaseRegistry) {
      log.info("Cannot delete dataset {}/{} from project {} due to target project config being disabled.", ownerID, datasetID, projectID)
      return
    }

    log.debug("deleting dataset {}/{} from project {} app DB", ownerID, datasetID, projectID)

    appDB.withTransaction(projectID) {
      it.deleteDatasetVisibilities(datasetID)
      it.deleteDatasetProjectLinks(datasetID)
      it.deleteSyncControl(datasetID)
      it.deleteInstallMessages(datasetID)
      it.deleteDatasetMeta(datasetID)
      it.deleteDataset(datasetID)
    }
  }

  private fun DeletedDataset.deleteFromCacheDB() {
    log.debug("deleting dataset {}/{} from cache DB", ownerID, datasetID)

    cacheDB.withTransaction {
      it.deleteInstallFiles(datasetID)
      it.deleteUploadFiles(datasetID)
      it.deleteDatasetMetadata(datasetID)
      it.deleteDatasetProjects(datasetID)
      it.deleteDatasetShareOffers(datasetID)
      it.deleteDatasetShareReceipts(datasetID)
      it.deleteImportControl(datasetID)
      it.deleteImportMessages(datasetID)
      it.deleteSyncControl(datasetID)
      it.deleteDataset(datasetID)
    }
  }

  private fun DeletedDataset.deleteFromS3(bucket: S3Bucket) {
    log.debug("deleting dataset {}/{} from S3", ownerID, datasetID)

    bucket.objects.list(prefix = S3Paths.datasetDir(ownerID, datasetID))
      .map { it.path }
      .let { bucket.objects.deleteAll(it) }
  }
}