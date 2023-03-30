package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.model.InstallStatuses
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.cache.model.DatasetImportStatus

fun DatasetStatusInfo(imp: DatasetImportStatus, inst: Map<ProjectID, InstallStatuses>): DatasetStatusInfo =
  DatasetStatusInfoImpl().also {
    it.import = DatasetImportStatus(imp)
    it.install = ArrayList(inst.size)

    inst.forEach { (projectID, installStatus) -> it.install.add(DatasetInstallStatusEntry(projectID, installStatus)) }
  }