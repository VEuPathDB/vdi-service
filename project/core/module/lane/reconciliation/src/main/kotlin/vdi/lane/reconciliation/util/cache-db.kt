package vdi.lane.reconciliation.util

import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.initializeDataset
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.withTransaction
import vdi.lane.reconciliation.ReconcilerTarget
import vdi.model.DatasetUploadStatus
import vdi.model.OriginTimestamp
import vdi.model.meta.DataType
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetType
import vdi.model.meta.DatasetVisibility
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

internal fun CacheDB.dropImportMessages(ctx: ReconcilerTarget) =
  ctx.safeExec("failed to delete import messages") { withTransaction { it.deleteImportMessages(ctx.datasetId.toDatasetID()) } }

internal fun CacheDB.tryInitDataset(ctx: ReconcilerTarget, importStatus: DatasetImportStatus?) {
  val meta = ctx.meta ?: DatasetMetadata(
    type           = DatasetType(DataType.of("unknown"), "unknown"),
    installTargets = emptySet(),
    visibility     = DatasetVisibility.Private,
    owner          = ctx.userId,
    name           = "unknown",
    origin         = "unknown",
    created        = OriginTimestamp
  )

  withTransaction { db ->
    db.initializeDataset(
      ctx.datasetId.toDatasetID(),
      meta,
      if (importStatus == null)
        DatasetUploadStatus.Failed
      else DatasetUploadStatus.Success,
      importStatus,
    )

    if (importStatus == DatasetImportStatus.Failed)
      db.tryInsertImportMessages(
        ctx.datasetId.toDatasetID(),
        listOf("dataset has no import-ready file and is in an incomplete state due to the absence of the install-ready data and/or manifest")
      )

    if (meta.revisionHistory != null)
      db.tryInsertRevisionLinks(meta.revisionHistory!!)

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
