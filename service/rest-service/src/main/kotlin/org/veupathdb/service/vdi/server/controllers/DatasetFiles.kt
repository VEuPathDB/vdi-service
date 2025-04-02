package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiIdFiles
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiIdFiles.*
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsVdiIdFiles
import org.veupathdb.service.vdi.genx.model.NotFoundError
import org.veupathdb.service.vdi.service.dataset.*
import org.veupathdb.vdi.lib.common.field.toDatasetIDOrNull
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated(adminOverride = ALLOW_ALWAYS)
class DatasetFiles(@Context request: ContainerRequest)
  : DatasetsVdiIdFiles
  , VdiDatasetsVdiIdFiles // DEPRECATED API
  , ControllerBase(request)
{

  override fun getDatasetsFilesByVdiId(vdId: String): GetDatasetsFilesByVdiIdResponse {
    val datasetID = vdId.toDatasetIDOrNull()
      ?: return GetDatasetsFilesByVdiIdResponse.respond404WithApplicationJson(NotFoundError())

    return GetDatasetsFilesByVdiIdResponse.respond200WithApplicationJson(
      if (maybeUserID == null) {
        listDatasetFilesForAdmin(datasetID)
      } else {
        listDatasetFilesForUser(userID.toUserID(), datasetID)
      }
    )
  }

  override fun getDatasetsFilesUploadByVdiId(vdId: String): GetDatasetsFilesUploadByVdiIdResponse {
    val datasetID = vdId.toDatasetIDOrNull()
      ?: return GetDatasetsFilesUploadByVdiIdResponse.respond404WithApplicationJson(NotFoundError())

    val obj = if (maybeUserID == null) {
      getUploadFileForAdmin(datasetID)
    } else {
      getUploadFileForUser(userID.toUserID(), datasetID)
    }

    return GetDatasetsFilesUploadByVdiIdResponse
      .respond200WithApplicationOctetStream(
        { obj.stream.use { inp -> inp.transferTo(it) } },
        GetDatasetsFilesUploadByVdiIdResponse
          .headersFor200()
          .withContentDisposition("attachment; filename=\"$datasetID-upload.zip\"")
      )
  }

  override fun getDatasetsFilesDataByVdiId(vdId: String): GetDatasetsFilesDataByVdiIdResponse {
    val datasetID = vdId.toDatasetIDOrNull()
      ?: return GetDatasetsFilesDataByVdiIdResponse.respond404WithApplicationJson(NotFoundError())

    val obj = if (maybeUserID == null) {
      getDataFileForAdmin(datasetID)
    } else {
      getDataFileForUser(userID.toUserID(), datasetID)
    }

    return GetDatasetsFilesDataByVdiIdResponse
      .respond200WithApplicationOctetStream(
        { obj.stream.use { inp -> inp.transferTo(it) } },
        GetDatasetsFilesDataByVdiIdResponse
          .headersFor200()
          .withContentDisposition("attachment; filename=\"$datasetID-data.zip\"")
      )
  }

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getDatasetsFilesByVdiId(vdiId)"))
  override fun getVdiDatasetsFilesByVdiId(vdiId: String) =
    VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesByVdiIdResponse(getDatasetsFilesByVdiId(vdiId))

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getDatasetsFilesUploadByVdiId(vdiId)"))
  override fun getVdiDatasetsFilesUploadByVdiId(vdiId: String) =
    VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesUploadByVdiIdResponse(getDatasetsFilesUploadByVdiId(vdiId))

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getDatasetsFilesDataByVdiId(vdiId)"))
  override fun getVdiDatasetsFilesDataByVdiId(vdiId: String) =
    VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesDataByVdiIdResponse(getDatasetsFilesDataByVdiId(vdiId))
}
