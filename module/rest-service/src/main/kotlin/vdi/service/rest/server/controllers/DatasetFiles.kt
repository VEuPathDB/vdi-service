package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles.*
import vdi.service.rest.server.services.dataset.*
import vdi.service.rest.util.fold
import vdi.service.rest.util.mapLeft

@Authenticated(adminOverride = ALLOW_ALWAYS)
class DatasetFiles(@Context request: ContainerRequest)
  : DatasetsVdiIdFiles
  , ControllerBase(request)
{
  override fun getDatasetsFilesByVdiId(rawID: String): GetDatasetsFilesByVdiIdResponse =
    when (maybeUser) {
      null -> listDatasetFilesForAdmin(DatasetID(rawID))
      else -> listDatasetFilesForUser(DatasetID(rawID))
    }

  override fun getDatasetsFilesUploadByVdiId(rawID: String): GetDatasetsFilesUploadByVdiIdResponse {
    return when (maybeUser) {
      null -> getUploadFileForAdmin(DatasetID(rawID))
      else -> getUploadFileForUser(DatasetID(rawID))
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
    when (maybeUser) {
      null -> getInstallReadyZipForAdmin(DatasetID(rawID))
      else -> getInstallReadyZipForUser(DatasetID(rawID))
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
}
