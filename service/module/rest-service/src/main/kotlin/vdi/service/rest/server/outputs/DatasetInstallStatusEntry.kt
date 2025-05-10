package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.lib.db.app.model.InstallStatuses
import vdi.service.rest.generated.model.DatasetInstallStatusEntry

fun DatasetInstallStatusEntry(projectID: ProjectID, status: InstallStatuses): DatasetInstallStatusEntry =
  vdi.service.rest.generated.model.DatasetInstallStatusEntryImpl().also {
    it.projectId = projectID

    it.metaStatus = status.meta.let(::DatasetInstallStatus)
    it.metaMessage = status.metaMessage

    it.dataStatus = status.data?.let(::DatasetInstallStatus)
    it.dataMessage = status.dataMessage
  }
