package vdi.core.pruner

import kotlinx.coroutines.sync.Mutex
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.app.model.DeleteFlag
import vdi.core.db.app.purgeDatasetControlTables
import vdi.core.db.app.withTransaction
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.withTransaction
import vdi.logging.logger
import vdi.core.metrics.Metrics
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.DatasetObjectStore
import vdi.core.s3.files.DataFileType
import vdi.core.s3.files.FlagFileType
import vdi.core.s3.files.MetaFileType
import vdi.core.s3.paths.*

/**
 * Dataset Pruner
 *
 * This object represents and performs the task of pruning old/dead datasets
 * from the VDI system.
 *
 * Datasets are considered "prunable" if they have been marked as soft-deleted
 * or obsoleted by a newer revision for longer than a configured amount of time.
 * After this point they are subject to the following operations:
 *
 * 1. Deletion from the control tables in all relevant application databases.
 * 2. Deletion from the control tables in the VDI cache database.
 * 3. Deletion of relevant objects in the object store.
 *
 * For datasets that have been soft-deleted, all objects will be purged from the
 * object store.  For datasets that have been obsoleted, the revised flag, user
 * upload zip, and VDI manifest file will be retained until the possible future
 * point where the latest revision (and thus the full revision history) is
 * soft-deleted.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
object Pruner {
  private val logger = logger()

  /**
   * Files to be kept in the object store for maintaining dataset revision
   * history details.
   */
  private val retainedRevisionHistoryFiles = arrayOf<(String) -> Boolean>(
    { FlagFilePath.matches(it) && it.endsWith(FlagFileType.Revised.fileName) },
    { MetaFilePath.matches(it) && it.endsWith(MetaFileType.Manifest.fileName) },
    { DataFilePath.matches(it) && (
      it.endsWith(DataFileType.RawUpload.fileName)
      || it.endsWith(DataFileType.ImportReady.fileName) // TODO: remove this when the async upload process is implemented
    ) },
  )

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
  @Suppress("unused")
  fun isLocked() = lock.isLocked

  private fun doPruning() {
    logger.info("starting dataset pruning job")

    val bucket = S3Api.newClient(config.s3Config).buckets[config.bucketName]
      ?: throw IllegalStateException("bucket ${config.bucketName} does not exist!")

    val dsm = DatasetObjectStore(bucket)

    // Threshold is the start of the retention window going backwards in time
    // from the current timestamp.  Timestamps before this threshold are outside
    // the retention window and subject to deletion.
    val threshold = OffsetDateTime.now()
      .minus(config.pruneAge.inWholeSeconds, ChronoUnit.SECONDS)

    // Get a list of datasets that have been marked as deleted in the internal
    // postgres database, then filter that list down to only those datasets that
    // were deleted long enough ago to be eligible for pruning.
    val deletable = cacheDB.selectDeletedDatasets()
      .asSequence()
      .map(::DeletionContext)
      .onEach { it.determinePrunableState(dsm, threshold) }
      .filterNot { it.state == PrunableState.NotPrunable }
      .toList()

    logger.info("found {} candidates for pruning", deletable.size)

    deletable.forEach {
      if (!it.hasBeenUninstalled())
        return@forEach

      try {
        when (it.state) {
          PrunableState.Deleted -> runPruneAll(bucket, it)
          PrunableState.Obsoleted -> runPruneObsolete(bucket, it)
          PrunableState.NotPrunable -> throw IllegalStateException("somehow got state ${it.state} in deletion loop")
        }
        Metrics.Pruner.count.inc()
      } catch (e: Throwable) {
        Metrics.Pruner.failed.inc()
        it.logger.error("pruning failed", e)
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
  private fun DeletionContext.hasBeenUninstalled() =
    projects.all { projectID ->
      appDB.accessor(projectID, dataType)
        .also { if (it == null) {
          logger.warn("install target {} is currently disabled; cannot be pruned", projectID)
        } }
        ?.let { db ->
          db.selectDataset(datasetID).let {
            if (it == null) {
              logger.warn("no control record in install target {}; this will not prevent pruning", projectID)
              true
            } else if (it.deletionState != DeleteFlag.DeletedAndUninstalled) {
              logger.warn("not successfully uninstalled from target {}; cannot be pruned", projectID)
              false
            } else {
              true
            }
          }
        } ?: false
    }

  /**
   * Tests the dataset directory to determine what action the pruner should take
   * for the dataset, then updates the receiver with the determined dataset
   * pruning state.
   *
   * If the dataset has a deletion flag that is outside the retention window, it
   * can be fully purged.  If it has a revision marker that is outside the
   * retention window, it can be partially purged.  Otherwise, it cannot be
   * pruned.
   *
   * @receiver Context details for the dataset being tested.  The receiver
   * context instance is mutated by this extension method.
   *
   * @param dos Object store dataset directory manager.
   *
   * @param threshold Start time for the dataset retention window.
   */
  private fun DeletionContext.determinePrunableState(dos: DatasetObjectStore, threshold: OffsetDateTime) {
    val dir = dos.getDatasetDirectory(ownerID, datasetID)

    // If the directory doesn't exist, then it has already been deleted.
    state = dir.takeIf(DatasetDirectory::exists)
      // If the dataset directory doesn't exist, log an error and mark the
      // dataset as not prunable for future investigation.
      .let { when (it) {
        null -> {
          Metrics.Pruner.conflict.inc()
          logger.error("dataset has records in the cache db but has been deleted from the object store")
          PrunableState.NotPrunable
        }
        else -> null
      } }
      .let { when (it) {
        // If the dataset directory does exist, we won't have a prune state yet.
        // Check if there is a delete flag.
        null -> dir.getDeleteFlag().lastModified()
          // If the delete flag exists...
          ?.let { time -> when {
            // and is from before (outside) the retention window, it is now old
            // enough to be subject to pruning.
            time.isBefore(threshold) -> PrunableState.Deleted

            // and is from after the retention window start, it is within the
            // retention window and is not yet prunable.
            else -> PrunableState.NotPrunable
          } }

        // If we already had a pruning state, just pass it through
        else -> it
      } }
      .let { when (it) {
        // If the dataset directory does exist, but doesn't have a prune state
        // yet, then there was no delete flag.  Check if there is a historical
        // revision flag.
        null -> dir.getRevisedFlag().lastModified()
          // If the revised flag exists...
          ?.let { time -> when {
            // and is from before (outside) the retention window, it is now old
            // enough to be subject to pruning.
            time.isBefore(threshold) -> PrunableState.Obsoleted

            // and is from after the retention window start, it is within the
            // retention window and is not yet prunable.
            else -> PrunableState.NotPrunable
          } }

        // If we already had a pruning state, just pass it through
        else -> it
      } }
      .let { when (it) {
        // If we haven't determined a pruner state by now, then the dataset
        // directory existed, but it didn't have deleted flag or revised flag,
        // which means the cache db deletion flag doesn't align with the object
        // store state.  Something must have gone wrong to get here; log an
        // error and mark the dataset as not prunable for future investigation.
        null -> {
          Metrics.Pruner.conflict.inc()
          logger.error("dataset is marked as deleted in the cache db but does" +
            " not have a deleted or revised flag in the object store")
          PrunableState.NotPrunable
        }

        else -> it
      } }
  }

  // region Shared Pruning Functionality

  private fun DeletionContext.deleteFromAppDB(installTarget: InstallTargetID) {
    if (AppDatabaseRegistry.contains(installTarget, dataType)) {
      logger.debug("deleting dataset control data from target {}", installTarget)
      appDB.withTransaction(installTarget, dataType) { it.purgeDatasetControlTables(datasetID) }
    } else {
      logger.info("cannot delete dataset from disabled target {}", installTarget)
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
    cacheDB.withTransaction {
      if (!retainRevisions) {
        it.deleteRevisions(it.selectOriginalDatasetID(datasetID))
      }

      it.deleteDataset(datasetID)
    }
  }

  // endregion Shared Pruning Functionality

  // region Prune All

  private fun runPruneAll(bucket: S3Bucket, ctx: DeletionContext) {
    ctx.logger.info("pruning deleted revision")

    // Delete records from app databases
    ctx.projects.forEach {
      ctx.logger.debug("removing deleted dataset app db records for target {}", it)
      ctx.deleteFromAppDB(it)
    }

    val revisions = cacheDB.selectRevisions(ctx.datasetID)

    ctx.deleteFromCacheDB(retainRevisions = false) // wipe revisions as the latest is being removed

    if (revisions == null) {
      bucket.pruneAllObjects(ctx)

      // If there is no revision history, then we only have to prune the current
      // dataset.
      return
    }

    for (revision in revisions.revisions) {
      // make sure the older revision records are removed from the cache db
      // just in case
      ctx.safely("failed to remove obsolete revision {} records from cache db", revision.revisionID) {
        ctx.logger.info("removing obsolete revision records for {} from cache db", revision.revisionID)
        // retainRevisionHistory is set to true here as the history has
        // already been deleted so we can avoid the extra query
        ctx.deleteFromCacheDB(true)
      }

      ctx.safely("failed to remove obsolete revision {} data from object store", revision.revisionID) {
        ctx.logger.info("removing obsolete revision data for {} from object store", revision.revisionID)
        bucket.pruneAllObjects(ctx.ownerID, revision.revisionID)
      }
    }
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

  // endregion Prune All

  // region Prune Obsolete Revision

  private fun runPruneObsolete(bucket: S3Bucket, ctx: DeletionContext) {
    ctx.logger.info("pruning obsolete revision")

    // Delete records from app databases
    ctx.projects.forEach {
      ctx.logger.debug("removing obsolete dataset app db records for target {}", it)
      ctx.deleteFromAppDB(it)
    }

    // If the revised flag exists, then the current dataset is not the latest
    // version.
    ctx.logger.debug("removing obsolete dataset cache db records")
    ctx.deleteFromCacheDB(retainRevisions = true) // retain revisions until the latest version is deleted

    bucket.pruneObsoleteRevision(ctx)

    return
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
    ctx.logger.debug("removing unnecessary object store data")

    objects.list(prefix = S3Paths.datasetDir(ctx.ownerID, ctx.datasetID))
      .asSequence()
      .map { it.path }
      .filterNot { path -> retainedRevisionHistoryFiles.any { it(path) } }
      .toList()
      .let { objects.deleteAll(it) }
  }

  // endregion Prune Obsolete Revision
}

