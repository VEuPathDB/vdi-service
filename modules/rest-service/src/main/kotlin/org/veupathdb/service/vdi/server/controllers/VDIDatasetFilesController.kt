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
  override fun getVdiDatasetsFilesUploadByVdId(vdId: String): VdiDatasetsVdIdFiles.GetVdiDatasetsFilesUploadByVdIdResponse {
    val datasetID = vdId.toDatasetIDOrNull() ?: throw NotFoundException()

    val obj = if (maybeUserID == null) {
      getUploadFileForAdmin(datasetID)
    } else {
      getUploadFileForUser(userID.toUserID(), datasetID)
    }

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesUploadByVdIdResponse
      .respond200WithApplicationOctetStream(
        { obj.stream.use { inp -> inp.transferTo(it) } },
        VdiDatasetsVdIdFiles.GetVdiDatasetsFilesUploadByVdIdResponse
          .headersFor200()
          .withContentDisposition("attachment; filename=\"$datasetID-upload.zip\"")
      )

  }

  @AllowAdminAuth
  override fun getVdiDatasetsFilesDataByVdId(vdId: String): VdiDatasetsVdIdFiles.GetVdiDatasetsFilesDataByVdIdResponse {
    val datasetID = vdId.toDatasetIDOrNull() ?: throw NotFoundException()

    val obj = if (maybeUserID == null) {
      getDataFileForAdmin(datasetID)
    } else {
      getDataFileForUser(userID.toUserID(), datasetID)
    }

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesDataByVdIdResponse
      .respond200WithApplicationOctetStream(
        { obj.stream.use { inp -> inp.transferTo(it) } },
        VdiDatasetsVdIdFiles.GetVdiDatasetsFilesDataByVdIdResponse
          .headersFor200()
          .withContentDisposition("attachment; filename=\"$datasetID-data.zip\"")
      )
  }
}