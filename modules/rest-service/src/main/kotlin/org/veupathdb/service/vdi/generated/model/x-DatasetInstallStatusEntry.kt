package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.model.InstallStatuses
import org.veupathdb.vdi.lib.common.field.ProjectID

fun DatasetInstallStatusEntry(projectID: ProjectID, status: InstallStatuses): DatasetInstallStatusEntry =
  DatasetInstallStatusEntryImpl().also {
    it.projectID = projectID

    it.metaStatus = status.meta?.let(::DatasetInstallStatus)
    it.metaMessage = status.metaMessage

    it.dataStatus = status.data?.let(::DatasetInstallStatus)
    it.dataMessage = status.dataMessage
  }