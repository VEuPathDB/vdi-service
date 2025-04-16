package vdi.lane.reconciliation.util

import org.veupathdb.vdi.lib.common.OriginTimestamp
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetImpl
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.withTransaction
import vdi.lane.reconciliation.ReconciliationContext
import vdi.lib.db.model.SyncControlRecord
import java.time.OffsetDateTime


internal fun CacheDB.updateImportStatus(ctx: ReconciliationContext, status: DatasetImportStatus) {
  try {
    withTransaction { it.upsertImportControl(ctx.datasetID, status) }
  } catch (e: Throwable) {
    ctx.logger.error("failed to update dataset import status to {}", status, e)
  }
}

internal fun CacheDB.requireSyncControl(ctx: ReconciliationContext) =
  ctx.safeExec("failed to fetch sync control record") { selectSyncControl(ctx.datasetID) }
    .require(ctx, "could not find cache db sync control record")

internal fun CacheDB.getCacheImportControl(ctx: ReconciliationContext, refresh: Boolean = false) =
  ctx.safeExec("failed to fetch import control from cache db") {
    if (refresh || ctx.importControl == null)
      ctx.importControl = selectImportControl(ctx.datasetID)
    ctx.importControl
  }

internal fun CacheDB.isMissingImportMessage(ctx: ReconciliationContext, refresh: Boolean = false) =
  ctx.safeExec("failed to fetch import messages from cache db") {
    if (refresh || ctx.hasImportMessages == null)
      ctx.hasImportMessages = selectImportMessages(ctx.datasetID).isEmpty()
    ctx.hasImportMessages!!
  }

internal fun CacheDB.getCacheDatasetRecord(ctx: ReconciliationContext) =
  ctx.safeExec("failed to query cache db for dataset record") { selectDataset(ctx.datasetID) }

internal fun CacheDB.requireCacheDatasetRecord(ctx: ReconciliationContext) =
  getCacheDatasetRecord(ctx)
    .require(ctx, "could not find dataset record in cache db")

internal fun CacheDB.dropImportMessages(ctx: ReconciliationContext) =
  ctx.safeExec("failed to delete import messages") { withTransaction { it.deleteImportMessages(ctx.datasetID) } }

internal fun CacheDB.tryInitDataset(ctx: ReconciliationContext, importStatus: DatasetImportStatus) {
  withTransaction { db ->
    db.tryInsertDataset(
      DatasetImpl(
      datasetID    = ctx.datasetID,
      typeName     = ctx.meta.type.name,
      typeVersion  = ctx.meta.type.version,
      ownerID      = ctx.userID,
      isDeleted    = ctx.hasDeleteFlag,
      created      = ctx.meta.created,
      importStatus = DatasetImportStatus.Queued, // this value is not used for inserts
      origin       = ctx.meta.origin,
      inserted     = OffsetDateTime.now(),
    )
    )

    db.tryInsertDatasetMeta(ctx.datasetID, ctx.meta)

    db.tryInsertDatasetProjects(ctx.datasetID, ctx.meta.projects)

    db.tryInsertImportControl(ctx.datasetID, importStatus)

    if (importStatus == DatasetImportStatus.Failed)
      db.tryInsertImportMessages(
        ctx.datasetID,
        "dataset has no import-ready file and is in an incomplete state due to the absence of the install-ready data and/or manifest"
      )

    db.tryInsertSyncControl(
      SyncControlRecord(
      datasetID     = ctx.datasetID,
      sharesUpdated = OriginTimestamp,
      dataUpdated   = OriginTimestamp,
      metaUpdated   = OriginTimestamp,
    )
    )

    ctx.manifest?.also {
      db.tryInsertUploadFiles(ctx.datasetID, it.inputFiles)
      db.tryInsertInstallFiles(ctx.datasetID, it.dataFiles)
    }
  }
}

internal fun CacheDB.isImportFailed(ctx: ReconciliationContext, refresh: Boolean = false) =
  getCacheImportControl(ctx, refresh) == DatasetImportStatus.Failed

internal fun CacheDB.isImportInvalid(ctx: ReconciliationContext, refresh: Boolean = false) =
  getCacheImportControl(ctx, refresh) == DatasetImportStatus.Invalid
