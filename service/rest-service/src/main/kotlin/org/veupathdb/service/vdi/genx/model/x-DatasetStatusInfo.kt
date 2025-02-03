package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetStatusInfo
import org.veupathdb.service.vdi.generated.model.DatasetStatusInfoImpl
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.app.model.InstallStatuses
import vdi.component.db.cache.model.DatasetImportStatus

fun DatasetStatusInfo(imp: DatasetImportStatus, inst: Map<ProjectID, InstallStatuses>): DatasetStatusInfo =
  DatasetStatusInfoImpl().also {
    it.import = DatasetImportStatus(imp)
    it.install = ArrayList(inst.size)

    inst.forEach { (projectID, installStatus) -> it.install.add(DatasetInstallStatusEntry(projectID, installStatus)) }
  }
