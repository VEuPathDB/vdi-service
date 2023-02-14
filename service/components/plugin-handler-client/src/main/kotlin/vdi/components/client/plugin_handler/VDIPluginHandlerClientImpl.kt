package vdi.components.client.plugin_handler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.contains
import io.foxcapades.lib.k.multipart.MultiPart
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import kotlin.io.path.name
import vdi.components.client.plugin_handler.model.*
import vdi.components.json.JSON

internal class VDIPluginHandlerClientImpl(
  private val pluginHandlerURI: URI,
  private val httpClient: HttpClient,
) : VDIPluginHandlerClient {
  private val log = LoggerFactory.getLogger(javaClass)

  override fun sendImportRequest(details: ImportRequestDetails, payload: Path): ImportResponse {
    log.trace("sendImportRequest(details={}, payload={})", details, payload)

    val multipartPublisher = MultiPart.createBody {
      withPart {
        fieldName = FieldName.Details
        withBody(JSON.writeValueAsBytes(details), ContentType.JSON)
      }

      withPart {
        fieldName = FieldName.Payload
        fileName = payload.name
        withBody(payload.toFile(), ContentType.OctetStream)
      }
    }

    log.debug("sending POST request to vdi-plugin-server {}", EP.Import)
    val response = httpClient.send(
      HttpRequest.newBuilder(pluginHandlerURI.resolve(EP.Import))
        .header(Header.ContentType, multipartPublisher.getContentTypeHeader())
        .POST(multipartPublisher.makePublisher())
        .build(),
      HttpResponse.BodyHandlers.ofInputStream()
    )

    log.debug("vdi-plugin-server {} responded with status code {}", EP.Import, response.statusCode())

    try {
      return when (response.statusCode()) {
        200  -> response.parseImportPayloadBody()
        400  -> response.parseImportResponseWithMessage()
        418  -> response.parseImportResponseWithWarnings()
        420  -> response.parseImportResponseWithWarnings()
        500  -> response.parseImportResponseWithMessage()
        else -> throw IllegalStateException("unexpected response code from vdi-plugin-server ${EP.Import}")
      }
    } catch (e: Throwable) {
      // If an exception is thrown at this point, then close the input stream as
      // we are not going to be returning it to the caller.
      response.body().close()

      // Rethrow the exception.
      throw e
    }
  }

  private fun HttpResponse<InputStream>.parseImportPayloadBody(): ImportResponse {
    log.trace("parseImportPayloadBody(this={})", this)
    requireContentType(ContentType.OctetStream, EP.Import)
    return ImportResponseWithPayload(body())
  }

  private fun HttpResponse<InputStream>.parseImportResponseWithMessage(): ImportResponse {
    log.trace("parseImportResponseWithMessage(this={})", this)
    requireContentType(ContentType.JSON, EP.Import)
    return ImportResponseWithMessage(
      when (statusCode()) {
        400  -> ImportResponseType.BadRequest
        500  -> ImportResponseType.ServerError
        else -> throw IllegalStateException("unexpected status code")
      },
      body().use { JSON.readTree(it) } .requireMessageObject(EP.Import)
    )
  }

  private fun HttpResponse<InputStream>.parseImportResponseWithWarnings(): ImportResponse {
    log.trace("parseImportResponseWithWarnings(this={})", this)
    requireContentType(ContentType.JSON, EP.Import)
    return ImportResponseWithWarnings(
      when (statusCode()) {
        418  -> ImportResponseType.ValidationFailure
        420  -> ImportResponseType.TransformationFailure
        else -> throw IllegalStateException("unexpected status code")
      },
      body().use { JSON.readTree(it) }.requireWarningsObject("/import")
    )
  }

  override fun sendInstallMetaRequest(meta: InstallMetaRequestMeta): InstallMetaResponse {
    log.trace("sendInstallMetaRequest(meta={})", meta)

    log.debug("sending POST request to vdi-plugin-server {}", EP.InstallMeta)
    val response = httpClient.send(
      HttpRequest.newBuilder(pluginHandlerURI.resolve(EP.InstallMeta))
        .header(Header.ContentType, ContentType.JSON)
        .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(meta)))
        .build(),
      HttpResponse.BodyHandlers.ofString()
    )

    log.debug("vdi-plugin-server {} responded with status code {}", EP.InstallMeta, response.statusCode())

    return when (response.statusCode()) {
      204  -> InstallMetaResponseNoContent
      400  -> response.parseMetaResponseWithMessage()
      500  -> response.parseMetaResponseWithMessage()
      else -> throw IllegalStateException("unexpected response code from vdi-plugin-server ${EP.InstallMeta} endpoint")
    }
  }

  private fun HttpResponse<String>.parseMetaResponseWithMessage(): InstallMetaResponse {
    log.trace("parseMetaResponseWithMessage(this={})", this)
    requireContentType(ContentType.JSON, EP.InstallMeta)
    return InstallMetaResponseWithMessage(
      when (statusCode()) {
        400  -> InstallMetaResponseType.BadRequest
        500  -> InstallMetaResponseType.ServerError
        else -> throw IllegalStateException("unexpected response code")
      },
      JSON.readTree(body()).requireMessageObject(EP.InstallMeta)
    )
  }

  override fun sendInstallDataRequest(details: InstallDataRequestDetails, payload: Path): InstallDataResponse {
    log.trace("sendInstallDataRequest(details={}, payload={})", details, payload)

    val multipart = MultiPart.createBody {
      withPart {
        fieldName = FieldName.Details
        withBody(JSON.writeValueAsBytes(details), ContentType.JSON)
      }

      withPart {
        fieldName = FieldName.Payload
        fileName = payload.name
        withBody(payload.toFile(), ContentType.OctetStream)
      }
    }

    log.debug("sending POST request to vdi-plugin-server {}", EP.InstallData)
    val response = httpClient.send(
      HttpRequest.newBuilder(pluginHandlerURI.resolve(EP.InstallData))
        .header(Header.ContentType, multipart.getContentTypeHeader())
        .POST(multipart.makePublisher())
        .build(),
      HttpResponse.BodyHandlers.ofString()
    )

    log.debug("vdi-plugin-server {} responded with status code {}", EP.InstallData, response.statusCode())

    return when(response.statusCode()) {
      200  -> response.parseDataResponseWithWarnings()
      400  -> response.parseDataResponseWithMessage()
      500  -> response.parseDataResponseWithMessage()
      else -> throw IllegalStateException("unexpected response code from vdi-plugin-server ${EP.InstallData}")
    }
  }

  private fun HttpResponse<String>.parseDataResponseWithWarnings(): InstallDataResponse {
    log.trace("parseDataResponseWithWarnings(this={})", this)
    requireContentType(ContentType.JSON, EP.InstallData)
    return InstallDataResponseWithWarnings(JSON.readTree(body()).requireWarningsObject(EP.InstallData))
  }

  private fun HttpResponse<String>.parseDataResponseWithMessage(): InstallDataResponse {
    log.trace("parseDataResponseWithMessage(this={})", this)
    requireContentType(ContentType.JSON, EP.InstallData)
    return InstallDataResponseWithMessage(
      when (statusCode()) {
        400  -> InstallDataResponseType.BadRequest
        500  -> InstallDataResponseType.ServerError
        else -> throw IllegalStateException("unexpected response code")
      },
      JSON.readTree(body()).requireMessageObject(EP.InstallData)
    )
  }


  override fun sendUninstallRequest(details: UninstallRequest): UninstallResponse {
    log.trace("sendUninstallRequest(details={})", details)

    log.debug("sending POST request to vdi-plugin-server {}", EP.Uninstall)
    val response = httpClient.send(
      HttpRequest.newBuilder(pluginHandlerURI.resolve(EP.Uninstall))
        .header(Header.ContentType, ContentType.JSON)
        .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(details)))
        .build(),
      HttpResponse.BodyHandlers.ofString(),
    )
    log.debug("vdi-plugin-server {} responded with status code {}", EP.Uninstall, response.statusCode())

    return when (response.statusCode()) {
      204  -> UninstallResponseNoContent
      400  -> response.parseUninstallResponseWithMessage()
      500  -> response.parseUninstallResponseWithMessage()
      else -> throw IllegalStateException("unexpected response code from vdi-plugin-server ${EP.Uninstall} endpoint")
    }
  }

  private fun HttpResponse<String>.parseUninstallResponseWithMessage(): UninstallResponse {
    log.trace("parseUninstallResponseWithMessage(this={})", this)
    requireContentType(ContentType.JSON, EP.Uninstall)
    return UninstallResponseWithMessage(
      when (statusCode()) {
        400  -> UninstallResponseType.BadRequest
        500  -> UninstallResponseType.ServerError
        else -> throw IllegalStateException("how did this happen to me")
      },
      JSON.readTree(body()).requireMessageObject(EP.Uninstall)
    )
  }

  private fun JsonNode.requireWarningsObject(ep: String): Collection<String> {
    if (!isObject)
      throw IllegalStateException("response from vdi-plugin-server $ep was of an unexpected JSON type")

    if (!contains(FieldName.Warnings))
      throw IllegalStateException("response from vdi-plugin-server $ep contained no warnings field")

    if (!get(FieldName.Warnings).isArray)
      throw IllegalStateException("response from vdi-plugin-server $ep contained a non-array warnings field")

    val warnings = ArrayList<String>(get(FieldName.Warnings).size())

    for (value in get(FieldName.Warnings))
      if (!value.isTextual)
        throw IllegalStateException("response from vdi-plugin-server $ep contained a non-textual entry in the warnings array")
      else
        warnings.add(value.textValue())

    return warnings
  }

  private fun JsonNode.requireMessageObject(ep: String): String {
    log.trace("requireMessageObject(this={}, ep={})", this, ep)

    if (!isObject)
      throw IllegalStateException("response from vdi-plugin-server $ep was an unexpected JSON type")

    if (!contains(FieldName.Message))
      throw IllegalStateException("response from vdi-plugin-server $ep contained no message field")

    if (!get(FieldName.Message).isTextual)
      throw IllegalStateException("response from vdi-plugin-server $ep message field was not textual")

    return get(FieldName.Message).textValue()
  }

  private fun HttpResponse<*>.requireContentType(ct: String, ep: String) {
    log.trace("requireContentType(this={}, ct={}, ep={})", this, ct, ep)

    val contentType: String = headers()
      .firstValue(Header.ContentType)
      .orElseThrow { IllegalStateException("response from vdi-plugin-server $ep contained no Content-Type header") }
      .split(';')[0]

    if (contentType != ct)
      throw IllegalStateException("response from vdi-plugin-server $ep was of an unexpected Content-Type (expected $ct, got $contentType)")
  }
}