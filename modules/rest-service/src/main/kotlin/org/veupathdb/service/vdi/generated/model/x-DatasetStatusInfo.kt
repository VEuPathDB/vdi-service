package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.db.app.model.InstallStatuses

fun DatasetStatusInfo(imp: vdi.component.db.cache.model.DatasetImportStatus, inst: Map<ProjectID, InstallStatuses>): DatasetStatusInfo =
  DatasetStatusInfoImpl().also {
    it.import = DatasetImportStatus(imp)
    it.install = ArrayList(inst.size)

    inst.forEach { (projectID, installStatus) -> it.install.add(DatasetInstallStatusEntry(projectID, installStatus)) }
  }