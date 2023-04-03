package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.db.app.model.InstallStatus

fun DatasetInstallStatus(status: InstallStatus): DatasetInstallStatus =
  when (status) {
    InstallStatus.Running            -> DatasetInstallStatus.RUNNING
    InstallStatus.Complete           -> DatasetInstallStatus.COMPLETE
    InstallStatus.FailedValidation   -> DatasetInstallStatus.FAILEDVALIDATION
    InstallStatus.FailedInstallation -> DatasetInstallStatus.FAILEDINSTALLATION
    InstallStatus.ReadyForReinstall  -> DatasetInstallStatus.READYFORREINSTALL
  }