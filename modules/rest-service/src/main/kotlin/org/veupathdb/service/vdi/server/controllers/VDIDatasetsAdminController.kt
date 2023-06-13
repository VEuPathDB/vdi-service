package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.ForbiddenException
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.db.UserDB
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest
import org.veupathdb.service.vdi.generated.model.DatasetPostResponse
import org.veupathdb.service.vdi.generated.model.InstallCleanupRequest
import org.veupathdb.service.vdi.generated.model.validate
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsAdmin
import org.veupathdb.service.vdi.service.datasets.createDataset
import org.veupathdb.service.vdi.service.datasets.listBrokenDatasets
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.install_cleanup.InstallCleaner
import vdi.component.install_cleanup.ReinstallTarget
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

  override fun postVdiDatasetsAdminInstallCleanup(
    authKey: String?,
    entity: InstallCleanupRequest?,
  ): VdiDatasetsAdmin.PostVdiDatasetsAdminInstallCleanupResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()

    if (entity == null)
      throw BadRequestException()

    if (entity.all == true) {
      InstallCleaner.cleanAll()
    } else if (entity.targets.isNullOrEmpty()) {
      InstallCleaner.cleanTargets(entity.targets.map { ReinstallTarget(DatasetID(it.datasetID), it.projectID) })
    }

    return VdiDatasetsAdmin.PostVdiDatasetsAdminInstallCleanupResponse.respond204()
  }

  override fun postVdiDatasetsAdminDeleteCleanup(
    authKey: String?
  ): VdiDatasetsAdmin.PostVdiDatasetsAdminDeleteCleanupResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()

    Pruner.tryPruneDatasets()

    return VdiDatasetsAdmin.PostVdiDatasetsAdminDeleteCleanupResponse.respond204()
  }

  override fun postVdiDatasetsAdminProxyUpload(
    authKey: String?,
    userID: Long?,
    entity: DatasetPostRequest?,
  ): VdiDatasetsAdmin.PostVdiDatasetsAdminProxyUploadResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()

    if (userID == null)
      throw BadRequestException("no target user ID provided")

    (entity ?: throw BadRequestException())
      .validate()
      .throwIfNotEmpty()

    val userID = UserID(userID)

    if (UserDB.userIsGuest(userID) != false)
      throw ForbiddenException("target user does not exist or is a guest user")

    val datasetID = DatasetID()

    createDataset(userID, datasetID, entity)

    return VdiDatasetsAdmin.PostVdiDatasetsAdminProxyUploadResponse
      .respond200WithApplicationJson(DatasetPostResponse(datasetID))
  }
}