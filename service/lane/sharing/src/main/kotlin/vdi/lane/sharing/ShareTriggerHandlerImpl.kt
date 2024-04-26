package vdi.lane.sharing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import org.veupathdb.vdi.lib.common.util.isNull
import vdi.component.async.WorkerPool
import vdi.component.db.app.AppDB
import vdi.component.db.app.AppDBTransaction
import vdi.component.db.app.AppDatabaseRegistry
import vdi.component.db.app.withTransaction
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.db.cache.model.DatasetShareOfferImpl
import vdi.component.db.cache.model.DatasetShareReceiptImpl
import vdi.component.db.cache.withTransaction
import vdi.component.metrics.Metrics
import vdi.component.modules.AbortCB
import vdi.component.modules.AbstractVDIModule
import vdi.component.s3.DatasetManager
import vdi.component.s3.files.DatasetShare
import java.sql.SQLException
import java.time.OffsetDateTime
import vdi.component.s3.files.DatasetShare as S3Share

private const val UniqueConstraintViolation = 1

private enum class ShareState { Yes, No, Absent }

private data class ShareInfo(
  val offer: ShareState,
  val receipt: ShareState,
) {
  inline val visibleInTarget
    get() = offer == ShareState.Yes && receipt == ShareState.Yes
}

/**
 * Share Trigger Event Handler
 *
 * This trigger handler processes trigger events for dataset shares being
 * created or removed.
 */
internal class ShareTriggerHandlerImpl(private val config: ShareTriggerHandlerConfig, abortCB: AbortCB)
  : ShareTriggerHandler
  , AbstractVDIModule("share-trigger-handler", abortCB)
{
  private val log = LoggerFactory.getLogger(javaClass)

  private val cacheDB = CacheDB()

  private val appDB = AppDB()

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.shareTriggerTopic, config.kafkaConsumerConfig)
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val wp = WorkerPool("share-workers", config.workQueueSize, config.workerPoolSize) {
      Metrics.shareQueueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown())
          kc.fetchMessages(config.shareTriggerMessageKey)
            .forEach { (userID, datasetID, source) ->
              log.debug("submitting job to share worker pool for dataset {}/{} from source {}", datasetID, userID, source)
              wp.submit { executeJob(userID, datasetID, dm) }
            }
      }
    }

    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private fun executeJob(userID: UserID, datasetID: DatasetID, dm: DatasetManager) {
    log.info("processing share trigger for dataset {}/{}", userID, datasetID)

    val dataset = cacheDB.selectDataset(datasetID)

    // If the dataset record is null, then no such dataset exists in the cache
    // database, which is weird because the dataset record is written to the
    // cache database synchronously when the dataset is initially submitted.
    if (dataset.isNull()) {
      log.warn("target dataset {}/{} does not exist in the internal cache database, skipping event", userID, datasetID)
      return
    }

    if (dataset.isDeleted) {
      log.info("target dataset {}/{} is marked as deleted in the internal cache database", userID, datasetID)
      purgeFromTargets(dataset)
      return
    }

    log.debug("looking up dataset directory for dataset {}/{}", userID, datasetID)
    val dir = dm.getDatasetDirectory(userID, datasetID)

    val cacheDBSyncControl = cacheDB.selectSyncControl(datasetID)

    if (cacheDBSyncControl == null) {
      log.info("skipping share event for dataset {}/{}: dataset does not yet have a sync control record", userID, datasetID)
      return
    }

    if (!dir.hasInstallReadyFile()) {
      log.info("skipping share event for dataset {}/{}: dataset hasn't yet been imported", userID, datasetID)
      return
    }

    val latestShareFileTimestamp = dir.getLatestShareTimestamp(cacheDBSyncControl.sharesUpdated)

    // If the newest file version in MinIO has a timestamp that is newer than
    // the timestamp recorded in the postgres cache database, the share is brand
    // new and the share should be synchronized for all target projects.
    if (latestShareFileTimestamp.isAfter(cacheDBSyncControl.sharesUpdated)) {
      synchronizeAll(dataset, dir.getShares(), latestShareFileTimestamp)
    }

    // Else, if the newest file version in MinIO has a timestamp that is equal
    // to (or somehow before?) the timestamp that appears in the cache db, check
    // each individual project to make sure everything is up-to-date.
    //
    // A dataset could be out of sync in a single project if the project was
    // temporarily disabled, added later, was externally modified, or is being
    // rebuilt.
    else {
      synchronizeWhereNeeded(dataset, dir.getShares(), latestShareFileTimestamp)
    }
  }

  private fun synchronizeAll(
    dataset: DatasetRecord,
    shares: Map<UserID, S3Share>,
    latestShareTimestamp: OffsetDateTime
  ) {
    synchronizeCacheDB(dataset, shares, latestShareTimestamp)
    dataset.projects.forEach { projectID ->
      if (projectID in AppDatabaseRegistry)
        synchronizeProject(projectID, dataset, shares, latestShareTimestamp)
      else
        log.info("dataset {}/{} target {} is not currently enabled, skipping share sync", dataset.ownerID, dataset.datasetID, projectID)
    }
  }

  private fun synchronizeWhereNeeded(
    dataset: DatasetRecord,
    shares: Map<UserID, S3Share>,
    latestShareTimestamp: OffsetDateTime
  ) {
    dataset.projects.forEach {
      val targetDB = appDB.accessor(it)

      if (targetDB == null) {
        log.info("dataset {}/{} target {} is not currently enabled, skipping share sync", dataset.ownerID, dataset.datasetID, it)
        return@forEach
      }

      val targetSyncControl = targetDB.selectDatasetSyncControlRecord(dataset.datasetID)

      if (targetSyncControl == null) {
        log.info("dataset {}/{} has never been synchronized with target {}, skipping share sync", dataset.ownerID, dataset.datasetID, it)
        return@forEach
      }

      if (latestShareTimestamp.isAfter(targetSyncControl.sharesUpdated)) {
        synchronizeProject(it, dataset, shares, latestShareTimestamp)
      } else {
        log.info("dataset {}/{} is up to date in target {}, skipping share sync", dataset.ownerID, dataset.datasetID, it)
      }
    }
  }

  private fun synchronizeCacheDB(
    dataset: DatasetRecord,
    shares: Map<UserID, DatasetShare>,
    latestShareTimestamp: OffsetDateTime,
  ) {
    log.info("processing shares for dataset {}/{} in internal cache database", dataset.ownerID, dataset.datasetID)

    // Get a set of all the share recipients for all the shares attached to this
    // dataset in the internal cache db.
    //
    // This set will be used to track all the share records that appear in the
    // internal cache DB that do not appear in S3.  Records will be removed from
    // this set as shares from S3 are processed, leaving only those share
    // records that no longer appear in S3.
    val cachedShares = cacheDB.selectSharesForDataset(dataset.datasetID)
      .asSequence()
      .map { it.recipientID }
      .toMutableSet()

    cacheDB.withTransaction { db ->

      // Iterate through all the shares that appear in S3...
      shares.forEach { (shareRecipientUserID, shareDetails) ->

        // Figure out what the state of the shares are in S3 (what files exist
        // and what they say).
        val shareState = computeShareState(shareDetails)

        // Process the share offer.  If S3 has a share offer file present, the
        // share offer action will be recorded in the internal cache database.
        // If S3 does not have a share offer file present, any share offer
        // record in the internal cache db will be removed.
        when (shareState.offer) {
          ShareState.Yes -> db.upsertDatasetShareOffer(
            DatasetShareOfferImpl(
              dataset.datasetID,
              shareRecipientUserID,
              VDIShareOfferAction.Grant
            )
          )

          ShareState.No -> db.upsertDatasetShareOffer(
            DatasetShareOfferImpl(
              dataset.datasetID,
              shareRecipientUserID,
              VDIShareOfferAction.Revoke
            )
          )

          ShareState.Absent -> db.deleteShareOffer(dataset.datasetID, shareRecipientUserID)
        }

        // Process the share receipt.  If S3 has a share receipt file present,
        // the share receipt action will be recorded in the internal cache
        // database.  If S3 does not have a share receipt file present, any
        // share receipt record in the internal cache db will be removed.
        when (shareState.receipt) {
          ShareState.Yes -> db.upsertDatasetShareReceipt(
            DatasetShareReceiptImpl(
              dataset.datasetID,
              shareRecipientUserID,
              VDIShareReceiptAction.Accept
            )
          )

          ShareState.No -> db.upsertDatasetShareReceipt(
            DatasetShareReceiptImpl(
              dataset.datasetID,
              shareRecipientUserID,
              VDIShareReceiptAction.Reject
            )
          )

          ShareState.Absent -> db.deleteShareReceipt(dataset.datasetID, shareRecipientUserID)
        }

        // We have processed the share details for the current recipient user.
        // Remove it from the cached shares set so that we don't purge it from
        // the database.
        cachedShares.remove(shareRecipientUserID)
      }

      // For all cached shares remaining in the set, purge them from the
      // internal cache database.
      cachedShares.forEach { shareRecipientUserID ->
        db.deleteShareOffer(dataset.datasetID, shareRecipientUserID)
        db.deleteShareReceipt(dataset.datasetID, shareRecipientUserID)
      }

      // Finally, update the sync control record.
      db.updateShareSyncControl(dataset.datasetID, latestShareTimestamp)
    }
  }

  private fun synchronizeProject(
    projectID: ProjectID,
    dataset: DatasetRecord,
    shares: Map<UserID, S3Share>,
    latestShareTimestamp: OffsetDateTime,
  ) {
    log.info("synchronizing shares for dataset {}/{} in project {}", dataset.ownerID, dataset.datasetID, projectID)

    appDB.withTransaction(projectID) { db ->
      // Get a set of the recipient user IDs for all the users that this
      // dataset has been shared with in the target database.
      //
      // This set will be used to track all the visibility records that appear
      // in the app DB that do not appear in S3.  Recipient user IDs will be
      // removed from this set as shares from S3 are processed, leaving only
      // those visibility/share records that no longer appear in S3.
      val visibilityRecords = db.selectDatasetVisibilityRecords(dataset.datasetID)
        .asSequence()
        .map { it.userID }
        .toMutableSet()

      // For all the shares (complete or partial) that appear in S3...
      shares.forEach { (shareRecipient, shareDetails) ->
        log.debug("examining share for dataset {}/{} to user {} in project {}", dataset.ownerID, dataset.datasetID, shareRecipient, projectID)

        // Figure out what's going on with the share, as in what share files
        // exist and what the files that do exist say.
        val shareState = computeShareState(shareDetails)

        // Remove the current recipient user id from the app db and cache db
        // record caches.
        visibilityRecords.remove(shareRecipient)

        // If the dataset should be visible to the recipient user as
        // determined by examining the S3 share state, attempt to insert a
        // visibility record.  Since a visibility record may have already
        // existed, or been created by a competing worker, unique constraint
        // violations will be ignored on insert.
        if (shareState.visibleInTarget) {
          log.debug("ensuring dataset {}/{} is visible to user {} in project {}", dataset.ownerID, dataset.datasetID, shareRecipient, projectID)
          db.tryInsertDatasetVisibility(dataset.ownerID, dataset.datasetID, projectID, shareRecipient)
        }

        // If the dataset should not be visible to the recipient user as
        // determined by examining the S3 share state, attempt to remove any
        // existing visibility record.
        else {
          log.info("removing dataset {}/{} visibility from user {} in project {} as per S3 share state", dataset.ownerID, dataset.datasetID, shareRecipient, projectID)
          db.deleteDatasetVisibility(dataset.datasetID, shareRecipient)
        }
      }

      // Because the app DB visibility record is also used to indicate the
      // visibility of a dataset to that dataset's owner, we need to remove the
      // owner user ID from the visibility records to avoid the following loop
      // from removing the dataset owner's visibility record.
      visibilityRecords.remove(dataset.ownerID)

      // All the visibility records remaining in the recipient ID set from the
      // target app database are invalid as they have no matching records in S3.
      // Purge them from the target app database.
      visibilityRecords.forEach { recipientUserID ->
        log.info("removing dataset {}/{} visibility from user {} in project {} as S3 contains no such share", dataset.ownerID, dataset.datasetID, recipientUserID, projectID)
        db.deleteDatasetVisibility(dataset.datasetID, recipientUserID)
      }

      // In case the dataset owner's visibility record was accidentally deleted
      // by a user, bug, or other process, ensure that the record exists in the
      // target app db.
      db.tryInsertDatasetVisibility(dataset.ownerID, dataset.datasetID, projectID, dataset.ownerID)

      db.updateSyncControlSharesTimestamp(dataset.datasetID, latestShareTimestamp)
    }
  }

  private fun purgeFromTargets(dataset: DatasetRecord) {
    log.info("purging dataset visibility records for dataset {}/{} from all target projects", dataset.ownerID, dataset.datasetID)

    dataset.projects.forEach { projectID ->

      if (projectID !in AppDatabaseRegistry) {
        log.warn("cannot purge dataset visibility records for dataset {}/{} from project {} as that target is not currently enabled", dataset.ownerID, dataset.datasetID, projectID)
        return@forEach
      }

      appDB.withTransaction(projectID) { db -> db.deleteDatasetVisibilities(dataset.datasetID) }
    }
  }

  private fun computeShareState(shareDetails: S3Share) =
    ShareInfo(
      offer = when (shareDetails.offer.load()?.action) {
        null                       -> ShareState.Absent
        VDIShareOfferAction.Grant  -> ShareState.Yes
        VDIShareOfferAction.Revoke -> ShareState.No
      },
      receipt = when (shareDetails.receipt.load()?.action) {
        null                         -> ShareState.Absent
        VDIShareReceiptAction.Accept -> ShareState.Yes
        VDIShareReceiptAction.Reject -> ShareState.No
      },
    )

  private fun AppDBTransaction.tryInsertDatasetVisibility(
    ownerID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    recipientID: UserID
  ) {
    try {
      insertDatasetVisibility(datasetID, recipientID)
      // This log is here because we only want an info level line for attempted
      // actions that actually resulted in something happening.
      log.info("created dataset visibility record from dataset {}/{} to recipient {} in project {}", ownerID, datasetID, recipientID, projectID)
    } catch (e: SQLException) {
      if (e.errorCode == UniqueConstraintViolation) {
        log.debug("share insert race condition: dataset {}/{} already shared with {}", ownerID, datasetID, recipientID)
      } else {
        throw e
      }
    }
  }
}