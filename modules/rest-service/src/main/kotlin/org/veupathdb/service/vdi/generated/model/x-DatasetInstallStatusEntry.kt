package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.model.InstallStatuses
import vdi.components.common.fields.ProjectID

fun DatasetInstallStatusEntry(projectID: ProjectID, status: InstallStatuses): DatasetInstallStatusEntry =
  DatasetInstallStatusEntryImpl().also {
    it.projectID = projectID

    it.metaStatus = status.meta?.let(::DatasetInstallStatus)
    it.metaMessage = status.metaMessage

    it.dataStatus = status.data?.let(::DatasetInstallStatus)
    it.dataMessage = status.dataMessage
  }