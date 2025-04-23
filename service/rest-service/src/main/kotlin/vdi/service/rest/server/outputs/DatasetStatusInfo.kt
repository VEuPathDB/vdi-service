package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetStatusInfo
import vdi.service.rest.generated.model.DatasetStatusInfoImpl
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.lib.db.app.model.InstallStatuses
import vdi.lib.db.cache.model.DatasetImportStatus

fun DatasetStatusInfo(imp: DatasetImportStatus, inst: Map<ProjectID, InstallStatuses>): DatasetStatusInfo =
  vdi.service.rest.generated.model.DatasetStatusInfoImpl().also {
    it.import = DatasetImportStatus(imp)
    it.install = ArrayList(inst.size)

    inst.forEach { (projectID, installStatus) -> it.install.add(DatasetInstallStatusEntry(projectID, installStatus)) }
  }
