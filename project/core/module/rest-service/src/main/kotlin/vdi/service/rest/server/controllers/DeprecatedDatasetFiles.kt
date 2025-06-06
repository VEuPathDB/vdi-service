package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import vdi.model.data.DatasetID
import vdi.service.rest.generated.resources.VdiDatasetsVdiIdFiles
import vdi.service.rest.server.services.dataset.*
import vdi.service.rest.util.fold
import vdi.service.rest.util.mapLeft

@Deprecated("to be removed after client update")
@Authenticated(adminOverride = ALLOW_ALWAYS)
class DeprecatedDatasetFiles(@Context request: ContainerRequest): VdiDatasetsVdiIdFiles, ControllerBase(request) {
  override fun getVdiDatasetsFilesByVdiId(vdiId: String) =
    VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesByVdiIdResponse(when (maybeUser) {
      null -> listDatasetFilesForAdmin(DatasetID(vdiId))
      else -> listDatasetFilesForUser(DatasetID(vdiId))
    })

  override fun getVdiDatasetsFilesUploadByVdiId(vdiId: String) =
    VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesUploadByVdiIdResponse(when (maybeUser) {
      null -> getUploadFileForAdmin(DatasetID(vdiId))
      else -> getUploadFileForUser(DatasetID(vdiId))
    }
      .mapLeft { obj ->
        VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesUploadByVdiIdResponse
          .respond200WithApplicationOctetStream(
            { obj.stream.use { inp -> inp.transferTo(it) } },
            VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesUploadByVdiIdResponse
              .headersFor200()
              .withContentDisposition("attachment; filename=\"$vdiId-upload.zip\"")
          )
      }
      .fold())

  override fun getVdiDatasetsFilesDataByVdiId(vdiId: String) =
    VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesDataByVdiIdResponse(when (maybeUser) {
      null -> getInstallReadyZipForAdmin(DatasetID(vdiId))
      else -> getInstallReadyZipForUser(DatasetID(vdiId))
    }
      .mapLeft { so ->
        VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesDataByVdiIdResponse
          .respond200WithApplicationOctetStream(
            { so.stream.use { inp -> inp.transferTo(it) } },
            VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesDataByVdiIdResponse
              .headersFor200()
              .withContentDisposition("attachment; filename=\"$vdiId-data.zip\"")
          )
      }
      .fold())

  override fun getVdiDatasetsFilesDocumentsByVdiIdAndFileName(
    vdiId: String,
    fileName: String
  ) =
    VdiDatasetsVdiIdFiles.GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(when (maybeUser) {
      null -> getUserDocumentForAdmin(DatasetID(vdiId), fileName)
      else -> getUserDocumentForUser(DatasetID(vdiId), fileName)
    }.delegate)
}
