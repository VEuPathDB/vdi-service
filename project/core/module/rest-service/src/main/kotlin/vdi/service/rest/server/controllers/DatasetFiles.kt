package vdi.service.rest.server.controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.StreamingOutput
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import java.io.File
import vdi.core.db.cache.CacheDB
import vdi.json.JSON
import vdi.model.meta.DatasetID
import vdi.model.misc.UploadErrorReport
import vdi.service.rest.generated.model.DatasetsVdiIdFilesFileNameFileName
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles.*
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.NotFoundError
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.server.services.dataset.files.*
import vdi.util.fn.fold
import vdi.util.fn.mapLeft

@Authenticated(adminOverride = ALLOW_ALWAYS)
class DatasetFiles(@Context request: ContainerRequest): DatasetsVdiIdFiles, ControllerBase(request) {
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
          .respond200WithApplicationZip(
            StreamingOutput { obj.stream.use { inp -> inp.transferTo(it) } },
            GetDatasetsFilesUploadByVdiIdResponse
              .headersFor200()
              .withContentDisposition(makeContentDisposition("$rawID-upload.zip")))
      }
      .fold()
  }

  override fun getDatasetsFilesInstallByVdiId(rawID: String): GetDatasetsFilesInstallByVdiIdResponse =
    when (maybeUser) {
      null -> getInstallReadyZipForAdmin(DatasetID(rawID))
      else -> getInstallReadyZipForUser(DatasetID(rawID))
    }
      .mapLeft { so ->
        GetDatasetsFilesInstallByVdiIdResponse
          .respond200WithApplicationZip(
            StreamingOutput { so.stream.use { inp -> inp.transferTo(it) } },
            GetDatasetsFilesInstallByVdiIdResponse
              .headersFor200()
              .withContentDisposition(makeContentDisposition("$rawID-data.zip")))
      }
      .fold()

  override fun getDatasetsFilesDocumentsByVdiIdAndFileName(vdiId: String, fileName: String) =
    when (maybeUser) {
      null -> getUserDocumentForAdmin(DatasetID(vdiId), fileName)
      else -> getUserDocumentForUser(DatasetID(vdiId), fileName)
    }

  override fun putDatasetsFilesDocumentsByVdiIdAndFileName(vdiId: String, fileName: String, entity: File?) =
    when (maybeUser) {
      null -> throw ForbiddenException("only users may put document files")
      else -> putUserDocument(DatasetID(vdiId), fileName, entity)
    }

  override fun getDatasetsFilesVariablePropertiesByVdiIdAndFileName(
    vdiId:    String,
    fileName: String,
    // IMPORTANT: java primitives are boxed by code-gen and must be marked as
    // nullable in kotlin, otherwise valid requests will return unexpected 404s.
    download: Boolean?,
  ) = getVariablePropertiesFile(DatasetID(vdiId), fileName, download ?: true, maybeUser == null)

  override fun putDatasetsFilesVariablePropertiesByVdiIdAndFileName(
    vdiId:    String,
    fileName: String,
    entity:   File?,
  ) = entity
    ?.let { putVariablePropertiesFile(DatasetID(vdiId), fileName, it, maybeUser == null) }
    ?: BadRequestError("upload content not provided").wrap()

  override fun getDatasetsFilesByVdiIdAndFileName(
    vdiId: String,
    fileName: DatasetsVdiIdFilesFileNameFileName,
    // IMPORTANT: java primitives are boxed by code-gen and must be marked as
    // nullable in kotlin, otherwise valid requests will return unexpected 404s.
    download: Boolean?,
  ) = getDatasetMetaFileJson(vdiId, fileName, maybeUser != null, download!!)

  private fun getDatasetMetaFileJson(
    vdiId: String,
    fileName: DatasetsVdiIdFilesFileNameFileName,
    forUser: Boolean,
    download: Boolean
  ): GetDatasetsFilesByVdiIdAndFileNameResponse {
    val cacheDbDatasetRecord = CacheDB().selectDataset(DatasetID(vdiId))

    if (cacheDbDatasetRecord == null || cacheDbDatasetRecord.isDeleted)
      return GetDatasetsFilesByVdiIdAndFileNameResponse.respond404WithApplicationJson(NotFoundError())

    val responseContent = when (fileName) {
      DatasetsVdiIdFilesFileNameFileName.METADATAJSON -> DatasetStore.getDatasetMeta(
        cacheDbDatasetRecord.ownerID,
        cacheDbDatasetRecord.datasetID,
      )

      DatasetsVdiIdFilesFileNameFileName.UPLOADERRORSJSON -> DatasetStore.getUploadErrorReport(
        cacheDbDatasetRecord.ownerID,
        cacheDbDatasetRecord.datasetID,
      )
    }

    val jacksonObject = JSON.convertValue(responseContent, ObjectNode::class.java)

    // remove the stacktrace information if the requester is a normal user.
    if (forUser)
      jacksonObject.remove(UploadErrorReport.Stacktrace)

    val response = Response.status(200)
      .type(MediaType.APPLICATION_JSON)
      .entity(StreamingOutput {
        JSON.writeValue(it, responseContent)
      })

    if (download)
      response.header(HttpHeaders.CONTENT_DISPOSITION, makeContentDisposition(fileName.value))

    return GetDatasetsFilesByVdiIdAndFileNameResponse(response.build())
  }
}
