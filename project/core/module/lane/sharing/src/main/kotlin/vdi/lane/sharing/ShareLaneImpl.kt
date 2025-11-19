package vdi.lane.sharing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import java.sql.SQLException
import java.time.OffsetDateTime
import vdi.core.async.WorkerPool
import vdi.core.db.app.*
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetRecord
import vdi.core.db.cache.model.ShareOfferRecord
import vdi.core.db.cache.model.ShareReceiptRecord
import vdi.core.db.cache.withTransaction
import vdi.core.metrics.Metrics
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.s3.files.shares.ShareOffer
import vdi.core.s3.files.shares.ShareReceipt
import vdi.core.s3.getLatestShareTimestamp
import vdi.logging.logger
import vdi.model.meta.*

/**
 * Share Trigger Event Handler
 *
 * This trigger handler processes trigger events for dataset shares being
 * created or removed.
 */
internal class ShareLaneImpl(private val config: ShareLaneConfig, abortCB: AbortCB)
  : ShareLane
  , AbstractVDIModule(abortCB, logger<ShareLane>())
{
  private val cacheDB = runBlocking { safeExec("failed to init Cache DB", ::CacheDB) }

  private val appDB = AppDB()

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.eventTopic, config.kafkaConfig)
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val wp = WorkerPool.create<ShareLane>(config.jobQueueSize, config.workerCount) {
      Metrics.shareQueueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown())
          kc.fetchMessages(config.eventMsgKey)
            .map { ShareContext(it, dm, logger) }
            .forEach {
              it.logger.debug("submitting share job for event from source {}", it.eventSource)
              wp.submit { it.executeJob() }
            }
      }
    }

    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private fun ShareContext.executeJob() {
    logger.info("processing share trigger")

    val dataset = cacheDB.selectDataset(datasetID)

    // If the dataset record is null, then no such dataset exists in the cache
    // database, which is weird because the dataset record is written to the
    // cache database synchronously when the dataset is initially submitted.
    if (dataset == null) {
      logger.warn("skipping event, dataset does not exist in the internal cache database")
      return
    }

    if (dataset.isDeleted) {
      logger.info("dataset is marked as deleted in the internal cache database")
      purgeFromTargets(dataset)
      return
    }

    logger.debug("looking up dataset directory")
    val dir = store.getDatasetDirectory(ownerID, datasetID)

    val cacheDBSyncControl = cacheDB.selectSyncControl(datasetID)

    if (cacheDBSyncControl == null) {
      logger.info("skipping share event; dataset does not yet have a sync control record")
      return
    }

    if (!dir.hasInstallReadyFile()) {
      logger.info("skipping share event; dataset hasn't yet been imported")
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

  private fun ShareContext.synchronizeAll(
    dataset: DatasetRecord,
    shares: Map<UserID, Pair<ShareOffer, ShareReceipt>>,
    latestShareTimestamp: OffsetDateTime,
  ) {
    synchronizeCacheDB(dataset, shares, latestShareTimestamp)
    dataset.projects.forEach { projectID ->
      if (AppDatabaseRegistry.contains(projectID, dataset.type))
        synchronizeProject(projectID, dataset, shares, latestShareTimestamp)
      else
        logger.info("skipping share sync; target {} is disabled", projectID)
    }
  }

  private fun ShareContext.synchronizeWhereNeeded(
    dataset: DatasetRecord,
    shares: Map<UserID, Pair<ShareOffer, ShareReceipt>>,
    latestShareTimestamp: OffsetDateTime,
  ) {
    dataset.projects.forEach {
      val targetDB = appDB.accessor(it, dataset.type)

      if (targetDB == null) {
        logger.info("skipping share sync; install target {} is disabled", it)
        return@forEach
      }

      val targetSyncControl = targetDB.selectDatasetSyncControlRecord(dataset.datasetID)

      if (targetSyncControl == null) {
        logger.info("skipping share sync; dataset never been synchronized with target {}", it)
        return@forEach
      }

      if (latestShareTimestamp.isAfter(targetSyncControl.sharesUpdated)) {
        synchronizeProject(it, dataset, shares, latestShareTimestamp)
      } else {
        logger.info("skipping share sync, shares are up to date in target {}", it)
      }
    }
  }

  private fun ShareContext.synchronizeCacheDB(
    dataset: DatasetRecord,
    shares: Map<UserID, Pair<ShareOffer, ShareReceipt>>,
    latestShareTimestamp: OffsetDateTime,
  ) {
    logger.info("synchronizing shares in internal cache database")

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
            ShareOfferRecord(dataset.datasetID, shareRecipientUserID, DatasetShareOffer.Action.Grant))

          ShareState.No -> db.upsertDatasetShareOffer(
            ShareOfferRecord(dataset.datasetID, shareRecipientUserID, DatasetShareOffer.Action.Revoke))

          ShareState.Absent -> db.deleteShareOffer(dataset.datasetID, shareRecipientUserID)
        }

        // Process the share receipt.  If S3 has a share receipt file present,
        // the share receipt action will be recorded in the internal cache
        // database.  If S3 does not have a share receipt file present, any
        // share receipt record in the internal cache db will be removed.
        when (shareState.receipt) {
          ShareState.Yes -> db.upsertDatasetShareReceipt(
            ShareReceiptRecord(dataset.datasetID, shareRecipientUserID, DatasetShareReceipt.Action.Accept))

          ShareState.No -> db.upsertDatasetShareReceipt(
            ShareReceiptRecord(dataset.datasetID, shareRecipientUserID, DatasetShareReceipt.Action.Reject))

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

  private fun ShareContext.synchronizeProject(
    installTarget: InstallTargetID,
    dataset: DatasetRecord,
    shares: Map<UserID, Pair<ShareOffer, ShareReceipt>>,
    latestShareTimestamp: OffsetDateTime,
  ) {
    logger.info("synchronizing shares for install target {}", installTarget)

    appDB.withTransaction(installTarget, dataset.type) { db ->
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
        logger.debug("examining share for for recipient {} in install target {}", shareRecipient, installTarget)

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
          logger.debug("ensuring dataset is visible to share recipient {} in install target {}", shareRecipient, installTarget)
          context(logger) {
            db.tryInsertDatasetVisibility(dataset.datasetID, installTarget, shareRecipient)
          }
        }

        // If the dataset should not be visible to the recipient user as
        // determined by examining the S3 share state, attempt to remove any
        // existing visibility record.
        else {
          logger.info("removing dataset visibility from user {} in install target {}", shareRecipient, installTarget)
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
        logger.info("removing dataset visibility from user {} in project {}; object store contains no such share", recipientUserID, installTarget)
        db.deleteDatasetVisibility(dataset.datasetID, recipientUserID)
      }

      // In case the dataset owner's visibility record was accidentally deleted
      // by a user, bug, or other process, ensure that the record exists in the
      // target app db.
      context(logger) {
        db.tryInsertDatasetVisibility(dataset.datasetID, installTarget, dataset.ownerID)
      }

      db.updateSyncControlSharesTimestamp(dataset.datasetID, latestShareTimestamp)
    }
  }

  private fun ShareContext.purgeFromTargets(dataset: DatasetRecord) {
    logger.info("purging dataset visibility records from all target projects")

    dataset.projects.forEach { projectID ->

      if (!AppDatabaseRegistry.contains(projectID, dataset.type)) {
        logger.warn("cannot purge dataset visibility records from disabled install target {}", projectID)
        return@forEach
      }

      appDB.withTransaction(projectID, dataset.type) { db -> db.deleteDatasetVisibilities(dataset.datasetID) }
    }
  }

  private fun computeShareState(shareDetails: Pair<ShareOffer, ShareReceipt>) =
    ShareInfo(
      offer = when (shareDetails.first.load()?.action) {
        null                       -> ShareState.Absent
        DatasetShareOffer.Action.Grant  -> ShareState.Yes
        DatasetShareOffer.Action.Revoke -> ShareState.No
      },
      receipt = when (shareDetails.second.load()?.action) {
        null                         -> ShareState.Absent
        DatasetShareReceipt.Action.Accept -> ShareState.Yes
        DatasetShareReceipt.Action.Reject -> ShareState.No
      },
    )

  context(logger: Logger)
  private fun AppDBTransaction.tryInsertDatasetVisibility(
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    recipientID: UserID,
  ) {
    try {
      insertDatasetVisibility(datasetID, recipientID)
      // This log is here because we only want an info level line for attempted
      // actions that actually resulted in something happening.
      logger.info("created dataset visibility record for recipient {} in install target {}", recipientID, installTarget)
    } catch (e: SQLException) {
      if (isUniqueConstraintViolation(e)) {
        logger.info("share insert race condition; already shared with recipient {}", recipientID)
      } else {
        throw e
      }
    }
  }
}
