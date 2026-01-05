@file:JvmName("DatasetPostService")
package vdi.service.rest.server.services.dataset

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import jakarta.ws.rs.BadRequestException
import org.veupathdb.lib.request.validation.ValidationErrors
import vdi.core.db.cache.CacheDB
import vdi.core.install.InstallTargetRegistry
import vdi.json.JSON
import vdi.model.meta.DatasetID
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPostRequestBody
import vdi.service.rest.generated.model.DatasetProxyPostRequestBody
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.inputs.toDatasetMeta
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.DatasetPostResponseBody
import vdi.service.rest.server.outputs.UnprocessableEntityError
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.generated.resources.Datasets.PostDatasetsResponse as PostResponse

fun ControllerBase.createDataset(
  datasetID:    DatasetID,
  entity:       DatasetPostRequestBody,
  uploadConfig: UploadConfig,
): PostResponse {
  val datasetMeta = entity.toDatasetMeta(userID)

  // Secondary validation for any additional JSON schema rules in service config
  datasetMeta.installTargets.asSequence()
    .mapNotNull(InstallTargetRegistry::get)
    .filter { it.metaValidation != null }
    .toList()
    .takeIf(Collection<*>::isNotEmpty)
    ?.also { targets ->
      val json = JSON.convertValue<ObjectNode>(datasetMeta)
      val validationErrors = ValidationErrors()

      targets.forEach {
        logger.debug("applying additional schema validation from install target {}", it.name)
        json.validate(it.metaValidation!!, validationErrors)
      }

      if (validationErrors.isNotEmpty)
        return UnprocessableEntityError(validationErrors).wrap()
    }

  entity.dataFile
    ?.let { verifyFileExtensions(it, datasetMeta.type) }
    ?.also { return it.wrap() }

  val uploadRefs = CacheDB()
    .initializeDataset(userID, datasetID, datasetMeta) {
      resolveDatasetFiles(
        entity.dataFile,
        entity.url,
        entity.docFile,
        entity.dataPropertiesFile,
        uploadConfig,
      )
    }

  submitUpload(datasetID, uploadRefs, datasetMeta, uploadConfig)

  return PostResponse.respond202WithApplicationJson(
    DatasetPostResponseBody(datasetID),
    PostResponse.headersFor202().withLocation(redirectURL(datasetID)),
  )
}

fun ControllerBase.createDataset(
  datasetID:    DatasetID,
  entity:       DatasetProxyPostRequestBody,
  uploadConfig: UploadConfig,
) {
  val datasetMeta = entity.toDatasetMeta(userID)

  entity.dataFile
    ?.let { verifyFileExtensions(it, datasetMeta.type) }
    ?.also { throw BadRequestException(it.message) }

  val uploadRefs  = CacheDB().initializeDataset(userID, datasetID, datasetMeta) {
    resolveDatasetFiles(entity.dataFile, entity.url, entity.docFile, emptyList(), uploadConfig)
  }

  submitUpload(datasetID, uploadRefs, datasetMeta, uploadConfig)
}
