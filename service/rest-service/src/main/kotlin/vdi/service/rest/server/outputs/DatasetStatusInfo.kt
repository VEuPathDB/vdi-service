package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetStatusInfo
import vdi.service.rest.generated.model.DatasetStatusInfoImpl
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.app.model.InstallStatuses
import vdi.component.db.cache.model.DatasetImportStatus

fun DatasetStatusInfo(imp: DatasetImportStatus, inst: Map<ProjectID, InstallStatuses>): vdi.service.rest.generated.model.DatasetStatusInfo =
  vdi.service.rest.generated.model.DatasetStatusInfoImpl().also {
    it.import = DatasetImportStatus(imp)
    it.install = ArrayList(inst.size)

    inst.forEach { (projectID, installStatus) -> it.install.add(DatasetInstallStatusEntry(projectID, installStatus)) }
  }
