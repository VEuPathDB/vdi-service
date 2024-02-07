package vdi.module.handler.meta.triggers

import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.or
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import java.time.OffsetDateTime

object DatasetReconciler {
  @JvmStatic
  fun reconcile(
    userID: UserID,
    datasetID: DatasetID,
    datasetMeta: VDIDatasetMeta,
    metaTimestamp: OffsetDateTime,
    datasetDirectory: DatasetDirectory,
    eventRouter: KafkaRouter,
  ) = ReconciliationBundle(userID, datasetID, datasetMeta, metaTimestamp, datasetDirectory, eventRouter).reconcile()

  private fun ReconciliationBundle.reconcile() {

  }

  private fun ReconciliationBundle.checkSyncStatus(): SyncIndicator {
    val cachedDatasetRecord = CacheDB.selectDataset(datasetID)
      ?: throw IllegalStateException("dataset $userID/$datasetID could not be found in the cache database")

    val cacheDBSyncControl = getOrCreateCacheDBSyncControl()

    val latestShareTimestamp = datasetDirectory.getLatestShareTimestamp(cacheDBSyncControl.sharesUpdated)
    val installDataTimestamp = datasetDirectory.getInstallReadyTimestamp() ?: cacheDBSyncControl.dataUpdated

    var sharesOutOfSync = cacheDBSyncControl.sharesUpdated.isBefore(latestShareTimestamp)
    var installOutOfSync = cacheDBSyncControl.dataUpdated.isBefore(installDataTimestamp)


  }

  private fun ReconciliationBundle.getOrCreateCacheDBSyncControl() =
    CacheDB.selectSyncControl(datasetID) or {
      VDISyncControlRecord(datasetID, OriginTimestamp, OriginTimestamp, OriginTimestamp)
        .also { CacheDB.withTransaction { db -> db.tryInsertSyncControl(it) } }
    }
}

private class SyncIndicator(
  val sharesOutOfSync: Boolean,
  val installOutOfSync: Boolean,
)

private class ReconciliationBundle(
  val userID: UserID,
  val datasetID: DatasetID,
  val datasetMeta: VDIDatasetMeta,
  val metaTimestamp: OffsetDateTime,
  val datasetDirectory: DatasetDirectory,
  val eventRouter: KafkaRouter,
)