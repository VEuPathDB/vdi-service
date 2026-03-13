package vdi.lane.reconciliation;

import vdi.core.db.app.AppDB;
import vdi.core.db.app.AppDBAccessor;
import vdi.core.db.app.model.DatasetRecord;
import vdi.core.db.app.model.DeleteFlag;
import vdi.core.db.model.SyncControlRecord;
import vdi.model.meta.DatasetMetadata;

import java.util.Objects;
import java.util.Set;

public final class AppDbUtils {
  public static boolean isFullyUninstalled(AppDB appDb, ReconcilerTarget ctx) {
    DatasetMetadata meta = Objects.requireNonNull(ctx.getMeta());
    Set<String> installTargets = meta.getInstallTargets();

    for (String installTarget : installTargets) {
      AppDBAccessor accessor = appDb.accessor(installTarget, meta.getType());

      if (accessor == null) {
        ctx.logger.warn("cannot check installation status for disabled target {}", installTarget);
        return false;
      }

      if (!isUninstalled(accessor, ctx))
        return false;
    }

    return true;
  }

  public static SyncControlRecord selectSyncControl(AppDBAccessor accessor, ReconcilerTarget ctx) {
    return ctx.safeExec(
      "failed to fetch dataset sync control record from " + accessor.getInstallTarget(),
      () -> accessor.selectDatasetSyncControlRecordById(ctx.getDatasetId())
    );
  }

  private static boolean isUninstalled(AppDBAccessor accessor, ReconcilerTarget ctx) {
    DatasetRecord targetRecord = selectDatasetRecord(accessor, ctx);

    // If the target record does not exist, then either VDI has been configured
    // to point at a clean database, the target database has been wiped, or
    // something has gone terribly wrong.  We'll assume it was intentional here
    // though and consider the dataset to be already uninstalled.
    if (targetRecord == null) {
      ctx.logger.warn(
        "attempted to check install status for dataset {}/{} in project {} but no such dataset record could be found;" +
          " assuming clean database and treating dataset as if it has been successfully uninstalled",
        ctx.getUserId(),
        ctx.getDatasetId(),
        accessor.getInstallTarget()
      );

      return true;
    }

    return targetRecord.getDeletionState() == DeleteFlag.DELETED_AND_UNINSTALLED;
  }

  private static DatasetRecord selectDatasetRecord(AppDBAccessor accessor, ReconcilerTarget ctx) {
    return ctx.safeExec(
      "failed to fetch dataset record from " + accessor.getInstallTarget(),
      () -> accessor.selectDatasetById(ctx.getDatasetId())
    );
  }
}
