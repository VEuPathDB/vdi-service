package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.ForbiddenException
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.generated.model.InstallCleanupRequest
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsAdmin
import org.veupathdb.service.vdi.service.datasets.listBrokenDatasets
import vdi.component.pruner.Pruner

class VDIDatasetsAdminController : VdiDatasetsAdmin {

  override fun getVdiDatasetsAdminListBroken(
    expanded: Boolean?,
    authKey: String?,
  ): VdiDatasetsAdmin.GetVdiDatasetsAdminListBrokenResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()

    return VdiDatasetsAdmin.GetVdiDatasetsAdminListBrokenResponse
      .respond200WithApplicationJson(listBrokenDatasets(expanded ?: true))
  }

  override fun postVdiDatasetsAdminReconcile(
    authKey: String?
  ): VdiDatasetsAdmin.PostVdiDatasetsAdminReconcileResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()


    TODO("Not yet implemented")
  }

  override fun postVdiDatasetsAdminInstallCleanup(
    authKey: String?,
    entity: InstallCleanupRequest?,
  ): VdiDatasetsAdmin.PostVdiDatasetsAdminInstallCleanupResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()

    TODO("Not yet implemented")
  }

  override fun postVdiDatasetsAdminDeleteCleanup(
    authKey: String?
  ): VdiDatasetsAdmin.PostVdiDatasetsAdminDeleteCleanupResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()

    Pruner.tryPruneDatasets()

    return VdiDatasetsAdmin.PostVdiDatasetsAdminDeleteCleanupResponse.respond204()
  }
}