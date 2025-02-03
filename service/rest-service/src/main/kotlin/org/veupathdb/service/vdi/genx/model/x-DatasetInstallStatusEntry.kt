package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetInstallStatusEntry
import org.veupathdb.service.vdi.generated.model.DatasetInstallStatusEntryImpl
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.app.model.InstallStatuses

fun DatasetInstallStatusEntry(projectID: ProjectID, status: InstallStatuses): DatasetInstallStatusEntry =
  DatasetInstallStatusEntryImpl().also {
    it.projectId = projectID

    it.metaStatus = status.meta?.let(::DatasetInstallStatus)
    it.metaMessage = status.metaMessage

    it.dataStatus = status.data?.let(::DatasetInstallStatus)
    it.dataMessage = status.dataMessage
  }
