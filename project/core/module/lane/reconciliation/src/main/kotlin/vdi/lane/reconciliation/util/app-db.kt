package vdi.lane.reconciliation.util

import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDBAccessor
import vdi.core.db.app.model.DeleteFlag
import vdi.lane.reconciliation.ReconcilerTarget
import vdi.model.meta.toDatasetID


internal fun AppDB.isFullyUninstalled(ctx: ReconcilerTarget): Boolean {
  ctx.meta!!.installTargets.forEach { projectID ->
    val appDB = accessor(projectID, ctx.meta!!.type)

    if (appDB == null) {
      ctx.logger.warn("cannot check installation status for disabled target {}", projectID)
      return false
    }

    appDB.isUninstalled(ctx) || return false
  }

  return true
}

internal fun AppDBAccessor.selectAppDatasetRecord(state: ReconcilerTarget) =
  state.safeExec("failed to fetch dataset record from $installTarget") { selectDataset(state.datasetId.toDatasetID()) }

internal fun AppDBAccessor.selectSyncControl(state: ReconcilerTarget) =
  state.safeExec("failed to fetch dataset sync control record from $installTarget") { selectDatasetSyncControlRecord(state.datasetId.toDatasetID()) }

internal fun AppDBAccessor.isUninstalled(ctx: ReconcilerTarget): Boolean {
  val targetRecord = selectAppDatasetRecord(ctx)

  // If the target record does not exist, then either VDI has been configured
  // to point at a clean database, the target database has been wiped, or
  // something has gone terribly wrong.  We'll assume it was intentional here
  // though and consider the dataset to be already uninstalled.
  if (targetRecord == null) {
    ctx.logger.warn(
      "attempted to check install status for dataset {}/{} in project {} but no such dataset record could be found;" +
      " assuming clean database and treating dataset as if it has been successfully uninstalled",
      ctx.userId,
      ctx.datasetId,
      installTarget
    )
    return true
  }

  if (targetRecord.deletionState != DeleteFlag.DeletedAndUninstalled) {
    return false
  }

  return true
}
