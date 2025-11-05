package vdi.core.plugin.client

import com.fasterxml.jackson.module.kotlin.readValue
import io.foxcapades.lib.k.multipart.MultiPart
import kotlinx.coroutines.future.await
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.DatasetMetadata
import vdi.model.data.DatasetType
import vdi.json.JSON
import vdi.json.toJSONString
import java.io.InputStream
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import vdi.core.plugin.client.response.EmptyResponse
import vdi.core.plugin.client.response.PluginResponse
import vdi.core.plugin.client.response.ScriptErrorResponse
import vdi.core.plugin.client.response.ServerErrorResponse
import vdi.core.plugin.client.response.StreamResponse
import vdi.core.plugin.client.response.ValidationResponse
import vdi.logging.logger
import vdi.io.plugin.PluginEndpoint
import vdi.io.plugin.requests.FormField
import vdi.io.plugin.requests.ImportRequest
import vdi.io.plugin.requests.InstallDataRequest
import vdi.io.plugin.requests.InstallMetaRequest
import vdi.io.plugin.requests.UninstallRequest
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.logging.mark
import vdi.model.EventID

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
  ): PluginResponse {
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
      when (val status = response.status) {
        PluginResponseStatus.Success -> StreamResponse(status, body)

        PluginResponseStatus.BasicValidationError -> ValidationResponse(status, JSON.readValue(body))
          .also { body.close() }

        PluginResponseStatus.CommunityValidationError -> StreamResponse(status, body)

        PluginResponseStatus.ScriptError -> ScriptErrorResponse(JSON.readValue(body))
          .also { body.close() }

        PluginResponseStatus.ServerError -> ServerErrorResponse(JSON.readValue(body))
          .also { body.close() }

        else -> {
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
  ): PluginResponse {
    val log = log.mark(eventID, datasetID)
    val uri = resolve(PluginEndpoint.InstallMeta)

    log.debug("submitting install-meta POST request to {} for project {}", uri, installTarget)

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, ContentType.JSON)
        .POST(HttpRequest.BodyPublishers.ofString(InstallMetaRequest(eventID, datasetID, installTarget, meta).toJSONString()))
        .build(),
      HttpResponse.BodyHandlers.ofString()
    ).await()

    return when (val status = response.status) {
      PluginResponseStatus.Success -> EmptyResponse(status)

      PluginResponseStatus.BasicValidationError,
      PluginResponseStatus.CommunityValidationError -> ValidationResponse(status, response.readJSON())

      PluginResponseStatus.ScriptError -> ScriptErrorResponse(response.readJSON())

      PluginResponseStatus.ServerError -> ServerErrorResponse(response.readJSON())

      else -> throw IllegalStateException("plugin server responded with invalid code ${response.statusCode()}")
    }
  }

  override suspend fun postInstallData(
    eventID: EventID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    meta: InputStream,
    manifest: InputStream,
    payload: InputStream,
  ): PluginResponse {
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

    return when (val status = response.status) {
      PluginResponseStatus.Success,
      PluginResponseStatus.BasicValidationError,
      PluginResponseStatus.CommunityValidationError -> ValidationResponse(status, response.readJSON())

      PluginResponseStatus.MissingDependencyError -> EmptyResponse(status)

      PluginResponseStatus.ServerError -> ServerErrorResponse(response.readJSON())

      PluginResponseStatus.ScriptError -> ServerErrorResponse(response.readJSON())

      else -> throw IllegalStateException("plugin server responded with invalid code ${response.statusCode()}")
    }
  }

  override suspend fun postUninstall(
    eventID: EventID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    type: DatasetType,
  ): PluginResponse {
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
        .POST(HttpRequest.BodyPublishers.ofString(UninstallRequest(eventID, datasetID, installTarget, type).toJSONString()))
        .build(),
      HttpResponse.BodyHandlers.ofString()
    ).await()

    return when (val status = response.status) {
      PluginResponseStatus.Success -> EmptyResponse(status)
      PluginResponseStatus.ScriptError -> ScriptErrorResponse(response.readJSON())
      PluginResponseStatus.ServerError -> ScriptErrorResponse(response.readJSON())
      else -> throw IllegalStateException("plugin server responded with invalid code ${response.statusCode()}")
    }
  }

  private inline val HttpResponse<*>.status: PluginResponseStatus
    get() = PluginResponseStatus.valueOf(statusCode())
  private inline fun <reified T> HttpResponse<String>.readJSON() =
    JSON.readValue<T>(body())
}
