package vdi.component.pruner

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DeletedDataset
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.LinkedList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

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

    val deletable = with(CacheDB.selectDeletedDatasets()) {
      val threshold = OffsetDateTime.now().minus(config.pruneAge.inWholeSeconds, ChronoUnit.SECONDS)
      filter { it.wasDeletedLongEnoughAgoToPrune(dsm, threshold) }
    }

    log.info("found {} candidates for pruning", deletable.size)

    deletable.forEach {
      try {
        runDelete(dsm, s3Bucket, it)
      } catch (e: Throwable) {
        log.error("failed to delete dataset ${it.ownerID}/${it.datasetID} due to exception:", e)
      }
    }

  }

  private fun DeletedDataset.wasDeletedLongEnoughAgoToPrune(dsm: DatasetManager, threshold: OffsetDateTime): Boolean {
    val dir = dsm.getDatasetDirectory(ownerID, datasetID)

    // If the directory doesn't exist, then it has already been deleted.
    if (!dir.exists()) {
      log.warn("dataset {}/{} still has a record in the cache db even though it has been deleted from S3", ownerID, datasetID)
      return false
    }

    if (!dir.hasDeleteFlag()) {
      log.error("dataset {}/{} is marked as deleted in the cache db but has no delete flag in S3", ownerID, datasetID)
      return false
    }

    return dir.getDeleteFlag()
      .lastModified()!!
      .isBefore(threshold)
  }

  private fun runDelete(dsm: DatasetManager, bucket: S3Bucket, ds: DeletedDataset) {
    log.info("hard-deleting dataset {}/{}", ds.ownerID, ds.datasetID)

    val dir  = dsm.getDatasetDirectory(ds.ownerID, ds.datasetID)
    val meta = dir.getMeta().load()!!

    // Delete records from app databases
    meta.projects.forEach { projectID -> ds.deleteFromAppDB(projectID) }

    // Delete records from cache DB
    ds.deleteFromCacheDB()

    // Delete objects from S3
    ds.deleteFromS3(bucket)
  }

  private fun DeletedDataset.deleteFromAppDB(projectID: ProjectID) {
    log.debug("deleting dataset {} from project {} app DB", datasetID, projectID)

    AppDB.withTransaction(projectID) {
      it.deleteDatasetVisibilities(datasetID)
      it.deleteDatasetProjectLinks(datasetID)
      it.deleteSyncControl(datasetID)
      it.deleteInstallMessages(datasetID)
      it.deleteDataset(datasetID)
    }
  }

  private fun DeletedDataset.deleteFromCacheDB() {
    log.debug("deleting dataset {} from cache DB", datasetID)

    CacheDB.withTransaction {
      it.deleteInstallFiles(datasetID)
      it.deleteUpdateFiles(datasetID)
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