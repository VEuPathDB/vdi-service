package vdi.service.rest.server.outputs

import vdi.model.data.InstallTargetID
import vdi.lib.db.app.model.InstallStatuses
import vdi.service.rest.generated.model.DatasetInstallStatusEntry
import vdi.service.rest.generated.model.DatasetInstallStatusEntryImpl

fun DatasetInstallStatusEntry(installTarget: InstallTargetID, status: InstallStatuses): DatasetInstallStatusEntry =
  DatasetInstallStatusEntryImpl().also {
    it.installTarget = installTarget

    it.metaStatus = status.meta.let(::DatasetInstallStatus)
    it.metaMessage = status.metaMessage

    it.dataStatus = status.data?.let(::DatasetInstallStatus)
    it.dataMessage = status.dataMessage
  }
