package vdi.core.plugin.client

import com.fasterxml.jackson.module.kotlin.readValue
import io.foxcapades.lib.k.multipart.MultiPart
import kotlinx.coroutines.future.await
import java.io.InputStream
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import vdi.core.plugin.client.response.*
import vdi.io.plugin.PluginEndpoint
import vdi.io.plugin.requests.*
import vdi.json.JSON
import vdi.json.toJSONString
import vdi.logging.logger
import vdi.logging.mark
import vdi.model.EventID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID
import vdi.io.plugin.responses.PluginResponseStatus as Status

internal class PluginHandlerClientImpl(private val config: PluginHandlerClientConfig): PluginHandlerClient {

  private val log = logger<PluginHandlerClient>()

  private inline val baseUri
    get() = URI.create("http://${config.address.host}:${config.address.port}")

  private fun resolve(ep: String) = baseUri.resolve(ep)

  override suspend fun postImport(
    eventID: EventID,
    datasetID: DatasetID,
    meta: DatasetMetadata,
    upload: InputStream,
  ): ImportResponse {
    val log = log.mark(eventID, datasetID)

    val multipart = MultiPart.createBody {
      withPart {
        fieldName = FormField.Details
        contentType(ContentType.JSON)
        withBody(ImportRequest(eventID, datasetID, ImportCounter.nextIndex(), meta).toJSONString())
      }

      withPart {
        fieldName = FormField.Payload
        fileName = "import-ready.zip"
        contentType(ContentType.Zip)
        withBody(upload)
      }
    }

    val uri = resolve(PluginEndpoint.Import)

    log.debug("submitting import POST request to {}", uri)

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, multipart.getContentTypeHeader())
        .POST(multipart.makePublisher())
        .build(),
      HttpResponse.BodyHandlers.ofInputStream()
    ).await()

    return response.body().let { body ->
      when (response.status) {
        Status.Success         -> StreamSuccessResponse(body)

        Status.ValidationError -> ValidationErrorResponse(JSON.readValue(body))
          .also { body.close() }

        Status.ScriptError     -> ScriptErrorResponse(JSON.readValue(body))
          .also { body.close() }

        Status.ServerError     -> ServerErrorResponse(JSON.readValue(body))
          .also { body.close() }

        else                   -> {
          body.close()
          throw IllegalStateException("plugin server responded with invalid code ${response.statusCode()}")
        }
      }
    }
  }

  override suspend fun postInstallMeta(
    eventID: EventID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    meta: DatasetMetadata,
  ): InstallMetaResponse {
    val log = log.mark(eventID, datasetID)
    val uri = resolve(PluginEndpoint.InstallMeta)

    log.debug("submitting install-meta POST request to {} for project {}", uri, installTarget)

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, ContentType.JSON)
        .POST(
          HttpRequest.BodyPublishers.ofString(
            InstallMetaRequest(
              eventID,
              datasetID,
              installTarget,
              meta
            ).toJSONString()
          )
        )
        .build(),
      HttpResponse.BodyHandlers.ofString()
    ).await()

    return when (response.status) {
      Status.Success         -> EmptySuccessResponse
      Status.ValidationError -> ValidationErrorResponse(response.readJSON())
      Status.ScriptError     -> ScriptErrorResponse(response.readJSON())
      Status.ServerError     -> ServerErrorResponse(response.readJSON())
      else                   -> throw IllegalStateException("plugin server responded with invalid code ${response.statusCode()}")
    }
  }

  override suspend fun postInstallData(
    eventID: EventID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    meta: InputStream,
    manifest: InputStream,
    payload: InputStream,
  ): InstallDataResponse {
    val log = log.mark(eventID, datasetID)

    val multipart = MultiPart.createBody {
      withPart {
        fieldName = FormField.Details
        contentType(ContentType.JSON)
        withBody(InstallDataRequest(eventID, datasetID, installTarget).toJSONString())
      }

      withPart {
        fieldName = FormField.Metadata
        contentType(ContentType.JSON)
        withBody(meta)
      }

      withPart {
        fieldName = FormField.Manifest
        contentType(ContentType.JSON)
        withBody(manifest)
      }

      withPart {
        fieldName = FormField.Payload
        fileName = "install-ready.zip"
        contentType(ContentType.Zip)
        withBody(payload)
      }
    }

    val uri = resolve(PluginEndpoint.InstallData)

    log.debug("submitting install-data POST request to {} for project {}", uri, installTarget)

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, multipart.getContentTypeHeader())
        .POST(multipart.makePublisher())
        .build(),
      HttpResponse.BodyHandlers.ofString()
    ).await()

    return when (response.status) {
      Status.Success                -> SuccessWithWarningsResponse(response.readJSON())
      Status.ValidationError        -> ValidationErrorResponse(response.readJSON())
      Status.MissingDependencyError -> MissingDependencyResponse(response.readJSON())
      Status.ScriptError            -> ServerErrorResponse(response.readJSON())
      Status.ServerError            -> ServerErrorResponse(response.readJSON())
      else                          -> throw IllegalStateException("plugin server responded with invalid code ${response.statusCode()}")
    }
  }

  override suspend fun postUninstall(
    eventID: EventID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    type: DatasetType,
  ): UninstallResponse {
    val log = log.mark(eventID, datasetID)
    val uri = resolve(PluginEndpoint.Uninstall)

    log.debug(
      "submitting uninstall POST request to {} for project {} (type {})",
      uri,
      installTarget,
      type.name
    )

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, ContentType.JSON)
        .POST(
          HttpRequest.BodyPublishers.ofString(
            UninstallRequest(
              eventID,
              datasetID,
              installTarget,
              type
            ).toJSONString()
          )
        )
        .build(),
      HttpResponse.BodyHandlers.ofString()
    ).await()

    return when (response.status) {
      Status.Success     -> EmptySuccessResponse
      Status.ScriptError -> ScriptErrorResponse(response.readJSON())
      Status.ServerError -> ScriptErrorResponse(response.readJSON())
      else               -> throw IllegalStateException("plugin server responded with invalid code ${response.statusCode()}")
    }
  }

  private inline val HttpResponse<*>.status: Status
    get() = Status.valueOf(statusCode())

  private inline fun <reified T> HttpResponse<String>.readJSON() =
    JSON.readValue<T>(body())
}
