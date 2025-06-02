package vdi.service.rest.server.outputs

import vdi.model.data.InstallTargetID
import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.service.rest.generated.model.DatasetStatusInfo
import vdi.service.rest.generated.model.DatasetStatusInfoImpl

fun DatasetStatusInfo(imp: DatasetImportStatus, inst: Map<InstallTargetID, InstallStatuses>): DatasetStatusInfo =
  DatasetStatusInfoImpl().also {
    it.import = DatasetImportStatus(imp)
    it.install = ArrayList(inst.size)

    inst.forEach { (projectID, installStatus) -> it.install.add(DatasetInstallStatusEntry(projectID, installStatus)) }
  }
