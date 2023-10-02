package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.AllowAdminAuth
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsVdIdFiles
import org.veupathdb.service.vdi.service.datasets.*
import org.veupathdb.vdi.lib.common.field.toDatasetIDOrNull
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated(allowGuests = false)
class VDIDatasetFilesController(@Context request: ContainerRequest) : VdiDatasetsVdIdFiles, ControllerBase(request) {

  @AllowAdminAuth
  override fun getVdiDatasetsFilesByVdId(vdId: String): VdiDatasetsVdIdFiles.GetVdiDatasetsFilesByVdIdResponse {
    val datasetID = vdId.toDatasetIDOrNull() ?: throw NotFoundException()

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesByVdIdResponse.respond200WithApplicationJson(
      if (maybeUserID == null) {
        listDatasetFilesForAdmin(datasetID)
      } else {
        listDatasetFilesForUser(userID.toUserID(), datasetID)
      }
    )
  }

  @AllowAdminAuth
  override fun getVdiDatasetsFilesUploadByVdIdAndFileName(
    vdId: String,
    fileName: String,
  ): VdiDatasetsVdIdFiles.GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse {
    val datasetID = vdId.toDatasetIDOrNull() ?: throw NotFoundException()

    val obj = if (maybeUserID == null) {
      getUploadFileForAdmin(datasetID, fileName)
    } else {
      getUploadFileForUser(userID.toUserID(), datasetID, fileName)
    }

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse
      .respond200WithApplicationOctetStream { obj.stream.use { inp -> inp.transferTo(it) } }
  }

  @AllowAdminAuth
  override fun getVdiDatasetsFilesDataByVdIdAndFileName(
    vdId: String,
    fileName: String,
  ): VdiDatasetsVdIdFiles.GetVdiDatasetsFilesDataByVdIdAndFileNameResponse {
    val datasetID = vdId.toDatasetIDOrNull() ?: throw NotFoundException()

    val obj = if (maybeUserID == null) {
      getDataFileForAdmin(datasetID, fileName)
    } else {
      getDataFileForUser(userID.toUserID(), datasetID, fileName)
    }

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesDataByVdIdAndFileNameResponse
      .respond200WithApplicationOctetStream { obj.stream.use { inp -> inp.transferTo(it) } }
  }

}