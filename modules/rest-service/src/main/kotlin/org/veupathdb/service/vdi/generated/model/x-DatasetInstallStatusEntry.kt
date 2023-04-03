package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.db.app.model.InstallStatuses

fun DatasetInstallStatusEntry(projectID: ProjectID, status: InstallStatuses): DatasetInstallStatusEntry =
  DatasetInstallStatusEntryImpl().also {
    it.projectID = projectID

    it.metaStatus = status.meta?.let(::DatasetInstallStatus)
    it.metaMessage = status.metaMessage

    it.dataStatus = status.data?.let(::DatasetInstallStatus)
    it.dataMessage = status.dataMessage
  }