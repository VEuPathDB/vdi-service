package vdi.service.rest.server.outputs

import vdi.core.db.app.model.InstallStatus
import vdi.service.rest.generated.model.DatasetInstallStatus

fun DatasetInstallStatus(status: InstallStatus): DatasetInstallStatus =
  when (status) {
    InstallStatus.Running            -> DatasetInstallStatus.RUNNING
    InstallStatus.Complete           -> DatasetInstallStatus.COMPLETE
    InstallStatus.FailedValidation   -> DatasetInstallStatus.FAILEDVALIDATION
    InstallStatus.FailedInstallation -> DatasetInstallStatus.FAILEDINSTALLATION
    InstallStatus.ReadyForReinstall  -> DatasetInstallStatus.READYFORREINSTALL
    InstallStatus.MissingDependency  -> DatasetInstallStatus.MISSINGDEPENDENCY
  }
