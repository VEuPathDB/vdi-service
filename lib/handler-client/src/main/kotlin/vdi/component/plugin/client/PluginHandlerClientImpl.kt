package vdi.component.plugin.client

import com.fasterxml.jackson.module.kotlin.readValue
import io.foxcapades.lib.k.multipart.MultiPart
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.intra.*
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.util.AtomicULong
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.json.toJSONString
import vdi.component.plugin.client.response.imp.*
import vdi.component.plugin.client.response.ind.*
import vdi.component.plugin.client.response.inm.*
import vdi.component.plugin.client.response.uni.*
import java.io.InputStream
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val requestCounter = AtomicULong()

private suspend fun incRequestCounter(): ULong {
  requestCounter += 1uL
  return requestCounter.get()
}

internal class PluginHandlerClientImpl(private val config: PluginHandlerClientConfig) : PluginHandlerClient {

  private val log = LoggerFactory.getLogger(javaClass)

  private inline val baseUri
    get() = URI.create("http://${config.address.host}:${config.address.port}")

  private fun resolve(ep: String) = baseUri.resolve(ep)

  override suspend fun postImport(datasetID: DatasetID, meta: VDIDatasetMeta, upload: InputStream): ImportResponse {
    val multipart = MultiPart.createBody {
      withPart {
        fieldName = FieldName.Details
        contentType("application/json; charset=utf-8")
        withBody(ImportRequest(datasetID, runBlocking { incRequestCounter() }, meta).toJSONString())
      }

      withPart {
        fieldName = FieldName.Payload
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

  override suspend fun postInstallMeta(datasetID: DatasetID, projectID: ProjectID, meta: VDIDatasetMeta): InstallMetaResponse {
    val uri = resolve(EP.InstallMeta)

    log.debug("submitting install-meta POST request to {} for project {} for dataset {}", uri, projectID, datasetID)

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, ContentType.JSON)
        .POST(HttpRequest.BodyPublishers.ofString(InstallMetaRequest(datasetID, incRequestCounter(), projectID, meta).toJSONString()))
        .build(),
      HttpResponse.BodyHandlers.ofString()
    ).await()

    return when (InstallMetaResponseType.fromCode(response.statusCode())) {
      InstallMetaResponseType.Success
      -> InstallMetaSuccessResponseImpl

      InstallMetaResponseType.BadRequest
      -> InstallMetaBadRequestResponseImpl(JSON.readValue<SimpleErrorResponse>(response.body()).message)

      InstallMetaResponseType.UnexpectedError
      -> InstallMetaUnexpectedErrorResponseImpl(JSON.readValue<SimpleErrorResponse>(response.body()).message)
    }
  }

  override suspend fun postInstallData(datasetID: DatasetID, projectID: ProjectID, payload: InputStream): InstallDataResponse {
    val multipart = MultiPart.createBody {
      withPart {
        fieldName = FieldName.Details
        contentType("application/json; charset=utf-8")
        withBody(InstallDataRequest(datasetID, runBlocking { incRequestCounter() }, projectID).toJSONString())
      }

      withPart {
        fieldName = FieldName.Payload
        fileName  = "data.tgz"
        withBody(payload)
      }
    }

    val uri = resolve(EP.InstallData)

    log.debug("submitting install-data POST request to {} for project {} for dataset {}", uri, projectID, datasetID)

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
      -> InstallDataValidationFailureResponseImpl(JSON.readValue<WarningResponse>(response.body()).warnings)

      InstallDataResponseType.MissingDependencies
      -> InstallDataMissingDependenciesResponseImpl(JSON.readValue<WarningResponse>(response.body()).warnings)

      InstallDataResponseType.UnexpectedError
      -> InstallDataUnexpectedErrorResponseImpl(JSON.readValue<SimpleErrorResponse>(response.body()).message)
    }
  }

  override suspend fun postUninstall(datasetID: DatasetID, projectID: ProjectID): UninstallResponse {
    val uri = resolve(EP.Uninstall)

    log.debug("submitting uninstall POST request to {} for project {} for dataset {}", uri, projectID, datasetID)

    val response = config.client.sendAsync(
      HttpRequest.newBuilder(uri)
        .header(Header.ContentType, ContentType.JSON)
        .POST(HttpRequest.BodyPublishers.ofString(UninstallRequest(datasetID, incRequestCounter(), projectID).toJSONString()))
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