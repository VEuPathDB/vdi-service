package vdi.core.plugin.client

import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.call.body
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.append
import io.ktor.utils.io.jvm.javaio.toInputStream
import io.ktor.utils.io.streams.asInput
import kotlinx.io.buffered
import java.io.InputStream
import java.net.URI
import vdi.core.plugin.client.model.DataPropertiesFile
import vdi.core.plugin.client.response.*
import vdi.io.plugin.PluginEndpoint
import vdi.io.plugin.requests.*
import vdi.io.plugin.responses.ServerInfoResponse
import vdi.json.JSON
import vdi.json.toJSONString
import vdi.logging.logger
import vdi.logging.mark
import vdi.model.EventID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID
import vdi.util.fn.Either
import vdi.io.plugin.responses.PluginResponseStatus as Status

internal class PluginHandlerClientImpl(
  name: String,
  private val config: PluginHandlerClientConfig,
): PluginHandlerClient {

  private val log = logger<PluginHandlerClient>().mark(pluginName = name)

  private inline val baseUri
    get() = URI.create("http://${config.address.host}:${config.address.port}")

  private fun resolve(ep: String) = baseUri.resolve(ep).toURL()

  private val JsonHeaders = Headers.build {
    append(HttpHeaders.ContentType, ContentType.Application.Json.withParameter("charset", "utf-8"))
  }

  override suspend fun getServerInfo(): Either<ServerInfoResponse, ServerErrorResponse> {
    log.info("retrieving plugin server build info")

    val response = config.client.get(url = resolve(PluginEndpoint.ServiceInfo))

//    val response = config.client.sendAsync(
//      HttpRequest.newBuilder().GET().build(),
//      HttpResponse.BodyHandlers.ofString(),
//    ).await()

    return when (response.status) {
      HttpStatusCode.OK -> Either.left(response.body() )
      else -> Either.right(response.readJSON())
    }
  }

  override suspend fun postImport(
    eventID:   EventID,
    datasetID: DatasetID,
    meta:      DatasetMetadata,
    upload:    InputStream,
  ): ImportResponse {
    val log = log.mark(eventID, datasetID)
    val uri = resolve(PluginEndpoint.Import)

    log.debug("submitting import POST request to {}", uri)

    val response = config.client.post(uri) {
      setBody(MultiPartFormDataContent(formData {
        append(
          FormField.Details,
          ImportRequest(eventID, datasetID, ImportCounter.nextIndex(), meta).toJSONString(),
          JsonHeaders,
        )

        append(
          FormField.Payload,
          InputProvider { upload.asInput().buffered() },
          Headers.build {
            append(HttpHeaders.ContentType, ContentType.Application.Zip)
            append(HttpHeaders.ContentDisposition, "filename=\"import-ready.zip\"")
          }
        )
      }))
    }

    return response.bodyAsChannel()
      .toInputStream()
      .let { body ->
      when (Status.valueOf(response.status.value)) {
        Status.Success -> StreamSuccessResponse(body)

        Status.ValidationError -> ValidationErrorResponse(JSON.readValue(body))
          .also { body.close() }

        Status.ScriptError -> ScriptErrorResponse(JSON.readValue(body))
          .also { body.close() }

        Status.ServerError -> ServerErrorResponse(JSON.readValue(body))
          .also { body.close() }

        else -> {
          body.close()
          throw IllegalStateException("plugin server responded with invalid code ${response.status}")
        }
      }
    }
  }

  override suspend fun postInstallMeta(
    eventID:       EventID,
    datasetID:     DatasetID,
    installTarget: InstallTargetID,
    meta:          DatasetMetadata,
    dataPropFiles: Iterable<DataPropertiesFile>,
  ): InstallMetaResponse {
    val log = log.mark(eventID, datasetID)
    val uri = resolve(PluginEndpoint.InstallMeta)

    log.debug("submitting install-meta POST request to {} for project {}", uri, installTarget)

    val response = config.client.post(uri) {
      setBody(MultiPartFormDataContent(formData {
        append(
          key     = FormField.Details,
          value   = InstallMetaRequest(eventID, datasetID, installTarget, meta).toJSONString(),
          headers = JsonHeaders,
        )

        dataPropFiles.forEach { append(
          key     = FormField.DataDict,
          value   = InputProvider { it.stream.asInput().buffered() },
          headers = Headers.build {
            append(HttpHeaders.ContentDisposition, "filename=\"${it.name}\"")
          },
        ) }
      }))
    }

    return when (Status.valueOf(response.status.value)) {
      Status.Success         -> EmptySuccessResponse
      Status.ValidationError -> ValidationErrorResponse(response.readJSON())
      Status.ScriptError     -> ScriptErrorResponse(response.readJSON())
      Status.ServerError     -> ServerErrorResponse(response.readJSON())
      else                   -> throw IllegalStateException("plugin server responded with invalid code ${response.status}")
    }
  }

  override suspend fun postInstallData(
    eventID:       EventID,
    datasetID:     DatasetID,
    installTarget: InstallTargetID,
    meta:          InputStream,
    manifest:      InputStream,
    payload:       InputStream,
    dataPropFiles: Iterable<DataPropertiesFile>,
  ): InstallDataResponse {
    val log = log.mark(eventID, datasetID)
    val uri = resolve(PluginEndpoint.InstallData)

    log.debug("submitting install-data POST request to {} for project {}", uri, installTarget)

    val response = config.client.post(uri) {
      setBody(MultiPartFormDataContent(formData {
        append(
          key     = FormField.Details,
          value   = InstallDataRequest(eventID, datasetID, installTarget).toJSONString(),
          headers = JsonHeaders,
        )

        append(
          key     = FormField.Metadata,
          value   = InputProvider { meta.asInput().buffered() },
          headers = JsonHeaders,
        )

        append(
          key     = FormField.Manifest,
          value   = InputProvider { manifest.asInput().buffered() },
          headers = JsonHeaders,
        )

        append(
          key     = FormField.Payload,
          value   = InputProvider { payload.asInput().buffered() },
          headers = Headers.build {
            append(HttpHeaders.ContentType, ContentType.Application.Zip)
            append(HttpHeaders.ContentDisposition, "filename=\"install-ready.zip\"")
          }
        )

        dataPropFiles.forEach { append(
          key     = FormField.DataDict,
          value   = InputProvider { it.stream.asInput().buffered() },
          headers = Headers.build {
            append(HttpHeaders.ContentDisposition, "filename=\"${it.name}\"")
          },
        ) }
      }))
    }

    return when (Status.valueOf(response.status.value)) {
      Status.Success                -> SuccessWithWarningsResponse(response.readJSON())
      Status.ValidationError        -> ValidationErrorResponse(response.readJSON())
      Status.MissingDependencyError -> MissingDependencyResponse(response.readJSON())
      Status.ScriptError            -> ScriptErrorResponse(response.readJSON())
      Status.ServerError            -> ServerErrorResponse(response.readJSON())
      else                          -> throw IllegalStateException("plugin server responded with invalid code ${response.status}")
    }
  }

  override suspend fun postUninstall(
    eventID:       EventID,
    datasetID:     DatasetID,
    installTarget: InstallTargetID,
    type:          DatasetType,
  ): UninstallResponse {
    val log = log.mark(eventID, datasetID)
    val uri = resolve(PluginEndpoint.Uninstall)

    log.debug(
      "submitting uninstall POST request to {} for project {} (type {})",
      uri,
      installTarget,
      type.name
    )

    val response = config.client.post(uri) {
      headers.append(HttpHeaders.ContentType, ContentType.Application.Json)
      setBody(UninstallRequest(
        eventID,
        datasetID,
        installTarget,
        type
      ).toJSONString())
    }

    return when (Status.valueOf(response.status.value)) {
      Status.Success     -> EmptySuccessResponse
      Status.ScriptError -> ScriptErrorResponse(response.readJSON())
      Status.ServerError -> ScriptErrorResponse(response.readJSON())
      else               -> throw IllegalStateException("plugin server responded with invalid code ${response.status}")
    }
  }

  private suspend inline fun <reified T> HttpResponse.readJSON() =
    JSON.readValue<T>(bodyAsChannel().toInputStream())
}
