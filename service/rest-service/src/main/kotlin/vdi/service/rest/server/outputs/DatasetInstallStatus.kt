package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetInstallStatus
import vdi.component.db.app.model.InstallStatus

fun DatasetInstallStatus(status: InstallStatus): vdi.service.rest.generated.model.DatasetInstallStatus =
  when (status) {
    InstallStatus.Running            -> vdi.service.rest.generated.model.DatasetInstallStatus.RUNNING
    InstallStatus.Complete           -> vdi.service.rest.generated.model.DatasetInstallStatus.COMPLETE
    InstallStatus.FailedValidation   -> vdi.service.rest.generated.model.DatasetInstallStatus.FAILEDVALIDATION
    InstallStatus.FailedInstallation -> vdi.service.rest.generated.model.DatasetInstallStatus.FAILEDINSTALLATION
    InstallStatus.ReadyForReinstall  -> vdi.service.rest.generated.model.DatasetInstallStatus.READYFORREINSTALL
    InstallStatus.MissingDependency  -> vdi.service.rest.generated.model.DatasetInstallStatus.MISSINGDEPENDENCY
  }
