package vdi.component.plugin.client

import com.fasterxml.jackson.module.kotlin.readValue
import io.foxcapades.lib.k.multipart.MultiPart
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.json.toJSONString
import vdi.component.plugin.client.model.*
import vdi.component.plugin.client.response.imp.*
import vdi.component.plugin.client.response.ind.*
import vdi.component.plugin.client.response.inm.*
import vdi.component.plugin.client.response.uni.*
import java.io.InputStream
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class PluginHandlerClientImpl(private val config: PluginHandlerClientConfig) :
  vdi.component.plugin.client.PluginHandlerClient {

  private val log = LoggerFactory.getLogger(javaClass)

  private inline val baseUri
    get() = URI.create("http://${config.address.host}:${config.address.port}")

  private fun resolve(ep: String) = baseUri.resolve(ep)

  override fun postImport(datasetID: DatasetID, meta: VDIDatasetMeta, upload: InputStream): ImportResponse {
    val multipart = MultiPart.createBody {
      withPart {
        fieldName = vdi.component.plugin.client.FieldName.Details
        contentType("application/json; charset=utf-8")
        withBody(ImportRequestDetails(datasetID, meta).toJSONString())
      }

      withPart {
        fieldName = vdi.component.plugin.client.FieldName.Payload
        fileName  = "upload.tgz"
        withBody(upload)
      }
    }

    val uri = resolve(vdi.component.plugin.client.EP.Import)

    log.debug("submitting import POST request to {} for dataset {}", uri, datasetID)

    val response = config.client.send(
      HttpRequest.newBuilder(uri)
        .header(vdi.component.plugin.client.Header.ContentType, multipart.getContentTypeHeader())
        .POST(multipart.makePublisher())
        .build(),
      HttpResponse.BodyHandlers.ofInputStream()
    )

    return response.body().let { body ->
      when (ImportResponseType.fromCode(response.statusCode())) {
        ImportResponseType.Success
        -> ImportSuccessResponseImpl(body)

        ImportResponseType.BadRequest
        -> ImportBadRequestResponseImpl(JSON.readValue<SimpleError>(body).message)
          .also { body.close() }

        ImportResponseType.ValidationError
        -> ImportValidationErrorResponseImpl(JSON.readValue<WarningsResponse>(body).warnings)
          .also { body.close() }

        ImportResponseType.UnhandledError
        -> ImportUnhandledErrorResponseImpl(JSON.readValue<SimpleError>(body).message)
          .also { body.close() }
      }
    }
  }

  override fun postInstallMeta(datasetID: DatasetID, projectID: ProjectID, meta: VDIDatasetMeta): InstallMetaResponse {
    val uri = resolve(vdi.component.plugin.client.EP.InstallMeta)

    log.debug("submitting install-meta POST request to {} for project {} for dataset {}", uri, projectID, datasetID)

    val response = config.client.send(
      HttpRequest.newBuilder(uri)
        .header(vdi.component.plugin.client.Header.ContentType, ContentType.JSON)
        .POST(HttpRequest.BodyPublishers.ofString(InstallMetaRequest(datasetID, projectID, meta).toJSONString()))
        .build(),
      HttpResponse.BodyHandlers.ofString()
    )

    return when (InstallMetaResponseType.fromCode(response.statusCode())) {
      InstallMetaResponseType.Success
      -> InstallMetaSuccessResponseImpl

      InstallMetaResponseType.BadRequest
      -> InstallMetaBadRequestResponseImpl(JSON.readValue<SimpleError>(response.body()).message)

      InstallMetaResponseType.UnexpectedError
      -> InstallMetaUnexpectedErrorResponseImpl(JSON.readValue<SimpleError>(response.body()).message)
    }
  }

  override fun postInstallData(datasetID: DatasetID, projectID: ProjectID, payload: InputStream): InstallDataResponse {
    val multipart = MultiPart.createBody {
      withPart {
        fieldName = vdi.component.plugin.client.FieldName.Details
        contentType("application/json; charset=utf-8")
        withBody(InstallDataRequestDetails(datasetID, projectID).toJSONString())
      }

      withPart {
        fieldName = vdi.component.plugin.client.FieldName.Payload
        fileName  = "data.tgz"
        withBody(payload)
      }
    }

    val uri = resolve(vdi.component.plugin.client.EP.InstallData)

    log.debug("submitting install-data POST request to {} for project {} for dataset {}", uri, projectID, datasetID)

    val response = config.client.send(
      HttpRequest.newBuilder(uri)
        .header(vdi.component.plugin.client.Header.ContentType, multipart.getContentTypeHeader())
        .POST(multipart.makePublisher())
        .build(),
      HttpResponse.BodyHandlers.ofString()
    )

    return when(InstallDataResponseType.fromCode(response.statusCode())) {
      InstallDataResponseType.Success
      -> InstallDataSuccessResponseImpl(JSON.readValue<WarningsResponse>(response.body()).warnings)

      InstallDataResponseType.BadRequest
      -> InstallDataBadRequestResponseImpl(JSON.readValue<SimpleError>(response.body()).message)

      InstallDataResponseType.ValidationFailure
      -> InstallDataValidationFailureResponseImpl(JSON.readValue<WarningsResponse>(response.body()).warnings)

      InstallDataResponseType.MissingDependencies
      -> InstallDataMissingDependenciesResponseImpl(JSON.readValue<WarningsResponse>(response.body()).warnings)

      InstallDataResponseType.UnexpectedError
      -> InstallDataUnexpectedErrorResponseImpl(JSON.readValue<SimpleError>(response.body()).message)
    }
  }

  override fun postUninstall(datasetID: DatasetID, projectID: ProjectID): UninstallResponse {
    val uri = resolve(vdi.component.plugin.client.EP.Uninstall)

    log.debug("submitting uninstall POST request to {} for project {} for dataset {}", uri, projectID, datasetID)

    val response = config.client.send(
      HttpRequest.newBuilder(uri)
        .header(vdi.component.plugin.client.Header.ContentType, ContentType.JSON)
        .POST(HttpRequest.BodyPublishers.ofString(UninstallDataRequest(datasetID, projectID).toJSONString()))
        .build(),
      HttpResponse.BodyHandlers.ofString()
    )

    return when(UninstallResponseType.fromCode(response.statusCode())) {
      UninstallResponseType.Success
      -> UninstallSuccessResponseImpl

      UninstallResponseType.BadRequest
      -> UninstallBadRequestResponseImpl(JSON.readValue<SimpleError>(response.body()).message)

      UninstallResponseType.UnexpectedError
      -> UninstallUnexpectedErrorResponseImpl(JSON.readValue<SimpleError>(response.body()).message)
    }
  }
}