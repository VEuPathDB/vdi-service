package vdi.service.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import vdi.service.generated.resources.DatasetsVdiIdFiles
import vdi.service.generated.resources.DatasetsVdiIdFiles.*
import vdi.service.generated.resources.VdiDatasetsVdiIdFiles
import vdi.service.server.services.dataset.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.service.util.fold
import vdi.service.util.mapLeft

@Authenticated(adminOverride = ALLOW_ALWAYS)
class DatasetFiles(@Context request: ContainerRequest)
  : DatasetsVdiIdFiles
  , VdiDatasetsVdiIdFiles // DEPRECATED API
  , ControllerBase(request)
{
  override fun getDatasetsFilesByVdiId(rawID: String): GetDatasetsFilesByVdiIdResponse =
    when (val it = maybeUserID) {
      null -> listDatasetFilesForAdmin(DatasetID(rawID))
      else -> listDatasetFilesForUser(it.toUserID(), DatasetID(rawID))
    }

  override fun getDatasetsFilesUploadByVdiId(rawID: String): GetDatasetsFilesUploadByVdiIdResponse {
    return when (val it = maybeUserID) {
      null -> getUploadFileForAdmin(DatasetID(rawID))
      else -> getUploadFileForUser(it.toUserID(), DatasetID(rawID))
    }
      .mapLeft { obj ->
        GetDatasetsFilesUploadByVdiIdResponse
          .respond200WithApplicationOctetStream(
            { obj.stream.use { inp -> inp.transferTo(it) } },
            GetDatasetsFilesUploadByVdiIdResponse
              .headersFor200()
              .withContentDisposition("attachment; filename=\"$rawID-upload.zip\"")
          )
      }
      .fold()
  }

  override fun getDatasetsFilesDataByVdiId(rawID: String): GetDatasetsFilesDataByVdiIdResponse =
    when (val it = maybeUserID) {
      null -> getInstallReadyZipForAdmin(DatasetID(rawID))
      else -> getInstallReadyZipForUser(it.toUserID(), DatasetID(rawID))
    }
      .mapLeft { so ->
        GetDatasetsFilesDataByVdiIdResponse
          .respond200WithApplicationOctetStream(
            { so.stream.use { inp -> inp.transferTo(it) } },
            GetDatasetsFilesDataByVdiIdResponse
              .headersFor200()
              .withContentDisposition("attachment; filename=\"$rawID-data.zip\"")
          )
      }
      .fold()

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
