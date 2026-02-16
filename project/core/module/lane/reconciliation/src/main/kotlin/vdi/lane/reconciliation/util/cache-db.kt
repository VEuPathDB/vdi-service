package vdi.lane.reconciliation.util

import java.time.OffsetDateTime
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetImpl
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.DatasetRecord
import vdi.core.db.cache.withTransaction
import vdi.core.db.model.SyncControlRecord
import vdi.lane.reconciliation.ReconcilerTarget
import vdi.model.OriginTimestamp
import vdi.model.meta.DatasetID
import vdi.model.meta.toDatasetID


internal fun CacheDB.updateImportStatus(ctx: ReconcilerTarget, status: DatasetImportStatus) {
  try {
    withTransaction { it.upsertImportControl(ctx.datasetId.toDatasetID(), status) }
  } catch (e: Throwable) {
    ctx.logger.error("failed to update dataset import status to {}", status, e)
  }
}

internal fun CacheDB.requireSyncControl(ctx: ReconcilerTarget) =
  ctx.safeExec("failed to fetch sync control record") { selectSyncControl(ctx.datasetId.toDatasetID()) }
    .require(ctx, "could not find cache db sync control record")

internal fun CacheDB.getCacheImportControl(ctx: ReconcilerTarget, refresh: Boolean = false) =
  ctx.safeExec("failed to fetch import control from cache db") {
    if (refresh || ctx.importControl == null)
      ctx.importControl = selectImportControl(ctx.datasetId.toDatasetID())
    ctx.importControl
  }

internal fun CacheDB.isMissingImportMessage(ctx: ReconcilerTarget, refresh: Boolean = false) =
  ctx.safeExec("failed to fetch import messages from cache db") {
    if (refresh || ctx.hasImportMessages == null)
      ctx.hasImportMessages = selectImportMessages(ctx.datasetId.toDatasetID()).isEmpty()
    ctx.hasImportMessages!!
  }

internal fun CacheDB.getCacheDatasetRecord(ctx: ReconcilerTarget) =
  ctx.safeExec("failed to query cache db for dataset record") { selectDataset(ctx.datasetId.toDatasetID()) }

internal fun CacheDB.requireCacheDatasetRecord(ctx: ReconcilerTarget) =
  getCacheDatasetRecord(ctx)
    .require(ctx, "could not find dataset record in cache db")

internal fun CacheDB.ensureCacheDatasetRecord(ctx: ReconcilerTarget): DatasetRecord {
  getCacheDatasetRecord(ctx)?.also { return it }

  // We need a record for the deleted dataset
  tryInitDataset(ctx, if (ctx.hasInstallReadyData()) DatasetImportStatus.Complete else DatasetImportStatus.Failed)

  return requireCacheDatasetRecord(ctx)
}

internal fun CacheDB.dropImportMessages(ctx: ReconcilerTarget) =
  ctx.safeExec("failed to delete import messages") { withTransaction { it.deleteImportMessages(ctx.datasetId.toDatasetID()) } }

internal fun CacheDB.tryInitDataset(ctx: ReconcilerTarget, importStatus: DatasetImportStatus) {
  withTransaction { db ->
    db.tryInsertDataset(DatasetImpl(
      datasetID    = DatasetID(ctx.datasetId),
      type         = ctx.meta!!.type,
      ownerID      = ctx.userId,
      isDeleted    = ctx.hasDeleteFlag(),
      created      = ctx.meta!!.created,
      importStatus = DatasetImportStatus.Queued, // this value is not used for inserts
      origin       = ctx.meta!!.origin,
      inserted     = OffsetDateTime.now(),
    ))

    db.tryInsertDatasetMeta(ctx.datasetId.toDatasetID(), ctx.meta!!)

    db.tryInsertDatasetProjects(ctx.datasetId.toDatasetID(), ctx.meta!!.installTargets)

    db.tryInsertImportControl(ctx.datasetId.toDatasetID(), importStatus)

    if (importStatus == DatasetImportStatus.Failed)
      db.tryInsertImportMessages(
        ctx.datasetId.toDatasetID(),
        listOf("dataset has no import-ready file and is in an incomplete state due to the absence of the install-ready data and/or manifest")
      )

    db.tryInsertSyncControl(SyncControlRecord(
      datasetID     = ctx.datasetId.toDatasetID(),
      sharesUpdated = OriginTimestamp,
      dataUpdated   = OriginTimestamp,
      metaUpdated   = OriginTimestamp,
    ))

    if (ctx.meta!!.revisionHistory != null)
      db.tryInsertRevisionLinks(ctx.meta!!.revisionHistory!!)

    ctx.manifest?.also {
      db.tryInsertUploadFiles(ctx.datasetId.toDatasetID(), it.userUploadFiles)
      db.tryInsertInstallFiles(ctx.datasetId.toDatasetID(), it.installReadyFiles)
    }
  }
}

internal fun CacheDB.isImportFailed(ctx: ReconcilerTarget, refresh: Boolean = false) =
  getCacheImportControl(ctx, refresh) == DatasetImportStatus.Failed

internal fun CacheDB.isImportInvalid(ctx: ReconcilerTarget, refresh: Boolean = false) =
  getCacheImportControl(ctx, refresh) == DatasetImportStatus.Invalid
