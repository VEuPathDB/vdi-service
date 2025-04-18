package vdi.component.pruner

import kotlinx.coroutines.sync.Mutex
import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.app.AppDB
import vdi.component.db.app.AppDatabaseRegistry
import vdi.component.db.app.model.DeleteFlag
import vdi.component.db.app.purgeDatasetControlTables
import vdi.component.db.app.withTransaction
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DeletedDataset
import vdi.component.db.cache.purgeDataset
import vdi.component.db.cache.withTransaction
import vdi.component.s3.DatasetManager
import vdi.component.s3.paths.S3Paths
import vdi.lib.metrics.Metrics
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

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

  private val lock = Mutex()

  private val config = PrunerConfig()

  private val cacheDB = CacheDB()

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

    val context = DeletionContext()

    deletable.forEach {
      if (!it.hasBeenUninstalled())
        return@forEach

      context.init(it)
      try {
        runDelete(s3Bucket, context)
        Metrics.Pruner.count.inc()
      } catch (e: Throwable) {
        Metrics.Pruner.failed.inc()
        context.logger.error("failed to delete dataset", e)
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
      val appDB = appDB.accessor(projectID, dataType)

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
    when {
      !dir.exists() -> {
        Metrics.Pruner.conflict.inc()
        log.error("dataset {}/{} still has a record in the cache db even though it has been deleted from S3", ownerID, datasetID)
        return false
      }

      dir.hasDeleteFlag() -> {
        return dir.getDeleteFlag()
          .lastModified()!!
          .isBefore(threshold)
      }

      dir.hasRevisedFlag() -> {
        return dir.getRevisedFlag()
          .lastModified()!!
          .isBefore(threshold)
      }

      else -> {
        Metrics.Pruner.conflict.inc()
        log.error("dataset {}/{} is marked as deleted in the cache db but has no delete or revision flag in S3", ownerID, datasetID)
        return false
      }
    }
  }

  private fun runDelete(bucket: S3Bucket, ds: DeletionContext) {
    log.info("hard-deleting dataset {}/{}", ds.ownerID, ds.datasetID)

    // Delete records from app databases
    ds.projects.forEach { projectID -> ds.deleteFromAppDB(projectID) }

    if (bucket.objects.contains(S3Paths.datasetRevisedFlagFile(ds.ownerID, ds.datasetID))) {
      // If the revised flag exists, then the current dataset is not the latest
      // version.
      ds.deleteFromCacheDB(retainRevisions = true) // retain revisions until the latest version is deleted
      bucket.pruneObsoleteRevision(ds)

      return
    }

    // If the revised flag doesn't exist, then this is the latest revision,
    // and has been marked for deletion.
    val revisions = cacheDB.selectRevisions(ds.datasetID)

    ds.deleteFromCacheDB(retainRevisions = false) // wipe revisions as the latest is being removed

    // If there is no revision history, then we only have to prune the
    // current dataset.
    if (revisions == null) {
      bucket.pruneAllObjects(ds)

      return
    }

    for (revision in revisions.records) {
      // make sure the older revision records are removed from the cache db
      // just in case
      ds.safely("failed to remove obsolete revision {} records from cache db", revision.revisionID) {
        ds.logger.info("removing obsolete revision records for {} from cache db", revision.revisionID)
        // retainRevisionHistory is set to true here as the history has
        // already been deleted so we can avoid the extra query
        cacheDB.withTransaction { it.purgeDataset(revision.revisionID, true) }
      }

      ds.safely("failed to remove obsolete revision {} data from object store", revision.revisionID) {
        ds.logger.info("removing obsolete revision data for {} from object store", revision.revisionID)
        bucket.pruneAllObjects(ds.ownerID, revision.revisionID)
      }
    }
  }

  /**
   * Executes the given function within a try/catch block, consuming any
   * exception and logging it to the receiver context's logger
   */
  private inline fun DeletionContext.safely(msg: String, arg: Any, fn: () -> Unit) {
    try {
      fn()
    } catch (e: Throwable) {
      logger.error(msg, arg, e)
    }
  }

  private fun DeletionContext.deleteFromAppDB(projectID: ProjectID) {
    if (AppDatabaseRegistry.contains(projectID, dataType)) {
      logger.debug("deleting dataset control data from target {}", projectID)
      appDB.withTransaction(projectID, dataType) { it.purgeDatasetControlTables(datasetID) }
    } else {
      logger.info("cannot delete dataset from disabled target {}", projectID)
    }
  }

  /**
   * Delete a dataset's cache records from the stack-internal database.
   *
   * @receiver Current dataset context.
   *
   * @param retainRevisions Whether revisions for the dataset should be kept.
   *
   * When pruning an obsolete dataset revision, this will be set to `true`.
   */
  private fun DeletionContext.deleteFromCacheDB(retainRevisions: Boolean) {
    logger.debug("deleting cache DB records")
    cacheDB.withTransaction { it.purgeDataset(datasetID, retainRevisions) }
  }

  /**
   * Prunes all objects in the object store for a target dataset.
   *
   * @receiver Object store bucket accessor.
   *
   * @param ctx Dataset deletion context information.
   */
  private fun S3Bucket.pruneAllObjects(ctx: DeletionContext) {
    ctx.logger.debug("deleting dataset from object store")
    pruneAllObjects(ctx.ownerID, ctx.datasetID)
  }

  /**
   * Prunes all objects in the object store for a target dataset.
   *
   * @receiver Object store bucket accessor.
   *
   * @param ownerID ID of the user that owns the dataset.
   *
   * @param datasetID ID of the target dataset.
   */
  private fun S3Bucket.pruneAllObjects(ownerID: UserID, datasetID: DatasetID) {
    objects.list(prefix = S3Paths.datasetDir(ownerID, datasetID))
      .map { it.path }
      .let { objects.deleteAll(it) }
  }

  /**
   * Prunes the unnecessary files left in the object store for an obsoleted
   * dataset revision.
   *
   * The original user upload and a revision marker file should be the only
   * objects retained for the old data.
   *
   * @receiver Object store bucket accessor.
   *
   * @param ctx Dataset deletion context information.
   */
  private fun S3Bucket.pruneObsoleteRevision(ctx: DeletionContext) {
    ctx.logger.debug("deleting obsolete dataset revision from object store")

    objects.list(prefix = S3Paths.datasetDir(ctx.ownerID, ctx.datasetID))
      .asSequence()
      .map { it.path }
      .filter {
        !it.endsWith(S3Paths.RevisionFlagFileName)
        && !it.endsWith(S3Paths.RawUploadZipName)
        && !it.endsWith(S3Paths.ImportReadyZipName) // TODO: remove this when the async upload process is implemented
      }
      .toList()
      .let { objects.deleteAll(it) }
  }
}

