package vdi.lib.plugin.client

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
import vdi.core.logging.logger
import vdi.lib.plugin.client.response.imp.*
import vdi.lib.plugin.client.response.ind.*
import vdi.lib.plugin.client.response.inm.*
import vdi.lib.plugin.client.response.uni.*
import vdi.model.api.internal.*

private const val JsonContentType = "application/json; charset=utf-8"


internal class PluginHandlerClientImpl(private val config: PluginHandlerClientConfig) : PluginHandlerClient {

  private val log = logger<PluginHandlerClient>()

  private inline val baseUri
    get() = URI.create("http://${config.address.host}:${config.address.port}")

  private fun resolve(ep: String) = baseUri.resolve(ep)

  override suspend fun postImport(datasetID: DatasetID, meta: DatasetMetadata, upload: InputStream): ImportResponse {
    val multipart = MultiPart.createBody {
      withPart {
        fieldName = FormField.Details
        contentType("application/json; charset=utf-8")
        withBody(ImportRequest(datasetID, ImportCounter.nextIndex(), meta).toJSONString())
      }

      withPart {
        fieldName = FormField.Payload
        fileName  = "upload.tgz"
        withBody(upload)
      }
    }

    val uri = resolve(EP.Import)

    log.debug("submitting import POST request to {} for dataset {}", uri, datasetID)

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, multipart.getContentTypeHeader())
        .POST(multipart.makePublisher())
        .build(),
      HttpResponse.BodyHandlers.ofInputStream()
    ).await()

    return response.body().let { body ->
      when (ImportResponseType.fromCode(response.statusCode())) {
        ImportResponseType.Success
        -> ImportSuccessResponseImpl(body)

        ImportResponseType.BadRequest
        -> ImportBadRequestResponseImpl(JSON.readValue<SimpleErrorResponse>(body).message)
          .also { body.close() }

        ImportResponseType.ValidationError
        -> ImportValidationErrorResponseImpl(JSON.readValue<WarningResponse>(body).warnings)
          .also { body.close() }

        ImportResponseType.UnhandledError
        -> ImportUnhandledErrorResponseImpl(JSON.readValue<SimpleErrorResponse>(body).message)
          .also { body.close() }
      }
    }
  }

  override suspend fun postInstallMeta(datasetID: DatasetID, installTarget: InstallTargetID, meta: DatasetMetadata): InstallMetaResponse {
    val uri = resolve(EP.InstallMeta)

    log.debug("submitting install-meta POST request to {} for project {} for dataset {}", uri, installTarget, datasetID)

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, ContentType.JSON)
        .POST(HttpRequest.BodyPublishers.ofString(InstallMetaRequest(datasetID, installTarget, meta).toJSONString()))
        .build(),
      HttpResponse.BodyHandlers.ofString()
    ).await()

    return when (InstallMetaResponseType.fromCode(response.statusCode())) {
      InstallMetaResponseType.Success
      -> InstallMetaSuccessResponseImpl

      InstallMetaResponseType.BadRequest
      -> InstallMetaBadRequestResponseImpl(JSON.readValue<SimpleErrorResponse>(response.body()).message)

      InstallMetaResponseType.UnexpectedError
      -> InstallMetaUnexpectedErrorResponseImpl(
        JSON.readValue<SimpleErrorResponse>(
          response.body()
        ).message
      )
    }
  }

  override suspend fun postInstallData(
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    meta: InputStream,
    manifest: InputStream,
    payload: InputStream,
  ): InstallDataResponse {
    val multipart = MultiPart.createBody {
      withPart {
        fieldName = FormField.Details
        contentType(JsonContentType)
        withBody(InstallDataRequest(datasetID, installTarget).toJSONString())
      }

      withPart {
        fieldName = FormField.Metadata
        contentType(JsonContentType)
        withBody(meta)
      }

      withPart {
        fieldName = FormField.Manifest
        contentType(JsonContentType)
        withBody(manifest)
      }

      withPart {
        fieldName = FormField.Payload
        fileName = "install-ready.zip"
        withBody(payload)
      }
    }

    val uri = resolve(EP.InstallData)

    log.debug("submitting install-data POST request to {} for project {} for dataset {}", uri, installTarget, datasetID)

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, multipart.getContentTypeHeader())
        .POST(multipart.makePublisher())
        .build(),
      HttpResponse.BodyHandlers.ofString()
    ).await()

    return when(InstallDataResponseType.fromCode(response.statusCode())) {
      InstallDataResponseType.Success
      -> InstallDataSuccessResponseImpl(JSON.readValue<WarningResponse>(response.body()).warnings)

      InstallDataResponseType.BadRequest
      -> InstallDataBadRequestResponseImpl(JSON.readValue<SimpleErrorResponse>(response.body()).message)

      InstallDataResponseType.ValidationFailure
      -> InstallDataValidationFailureResponseImpl(
        JSON.readValue<WarningResponse>(
          response.body()
        ).warnings
      )

      InstallDataResponseType.MissingDependencies
      -> InstallDataMissingDependenciesResponseImpl(JSON.readValue<WarningResponse>(response.body()).warnings)

      InstallDataResponseType.UnexpectedError
      -> InstallDataUnexpectedErrorResponseImpl(
        JSON.readValue<SimpleErrorResponse>(
          response.body()
        ).message
      )
    }
  }

  override suspend fun postUninstall(datasetID: DatasetID, installTarget: InstallTargetID, type: DatasetType): UninstallResponse {
    val uri = resolve(EP.Uninstall)

    log.debug("submitting uninstall POST request to {} for project {} for dataset {} (type {})", uri, installTarget, datasetID, type.name)

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, ContentType.JSON)
        .POST(HttpRequest.BodyPublishers.ofString(UninstallRequest(datasetID, installTarget, type).toJSONString()))
        .build(),
      HttpResponse.BodyHandlers.ofString()
    ).await()

    return when(UninstallResponseType.fromCode(response.statusCode())) {
      UninstallResponseType.Success
      -> UninstallSuccessResponseImpl

      UninstallResponseType.BadRequest
      -> UninstallBadRequestResponseImpl(JSON.readValue<SimpleErrorResponse>(response.body()).message)

      UninstallResponseType.UnexpectedError
      -> UninstallUnexpectedErrorResponseImpl(JSON.readValue<SimpleErrorResponse>(response.body()).message)
    }
  }
}
