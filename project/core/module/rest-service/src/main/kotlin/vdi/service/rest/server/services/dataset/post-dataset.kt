@file:JvmName("DatasetPostService")
package vdi.service.rest.server.services.dataset

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import org.veupathdb.lib.request.validation.ValidationErrors
import vdi.core.db.cache.CacheDB
import vdi.core.install.InstallTargetRegistry
import vdi.json.JSON
import vdi.model.data.DatasetID
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPostRequestBody
import vdi.service.rest.generated.model.DatasetProxyPostRequestBody
import vdi.service.rest.generated.resources.Datasets
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.inputs.cleanup
import vdi.service.rest.server.inputs.toDatasetMeta
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.DatasetPostResponseBody
import vdi.service.rest.server.outputs.UnprocessableEntityError
import vdi.service.rest.server.outputs.wrap

fun <T: ControllerBase> T.createDataset(
  datasetID: DatasetID,
  entity: DatasetPostRequestBody,
  uploadConfig: UploadConfig,
): Datasets.PostDatasetsResponse {
  entity.cleanup()

  with (entity.validate()) {
    if (isNotEmpty)
      return UnprocessableEntityError(this).wrap()
  }

  val datasetMeta = entity.toDatasetMeta(userID)

  // Secondary validation for any additional json schema rules in service config
  datasetMeta.installTargets.asSequence()
    .map { InstallTargetRegistry[it]!! }
    .filter { it.metaValidation != null }
    .toList()
    .takeIf { it.isNotEmpty() }
    ?.also { targets ->
      logger.debug("testing {} against {} additional json schema definitions", datasetID, uploadConfig)
      val json = JSON.convertValue<ObjectNode>(datasetMeta)
      val validationErrors = ValidationErrors()

      targets.forEach { json.validate(it.metaValidation!!, validationErrors) }

      if (validationErrors.isNotEmpty)
        return UnprocessableEntityError(validationErrors).wrap()
    }

  val uploadRefs = CacheDB().initializeDataset(userID, datasetID, datasetMeta) {
    resolveDatasetFiles(entity.dataFiles, entity.url, entity.docFiles, uploadConfig)
  }

  submitUpload(datasetID, uploadRefs, datasetMeta, uploadConfig)

  return Datasets.PostDatasetsResponse.respond202WithApplicationJson(
    DatasetPostResponseBody(datasetID),
    Datasets.PostDatasetsResponse.headersFor202().withLocation(redirectURL(datasetID)),
  )
}

fun <T: ControllerBase> T.createDataset(datasetID: DatasetID, entity: DatasetProxyPostRequestBody, uploadConfig: UploadConfig) {
  val datasetMeta = entity.toDatasetMeta(userID)
  val uploadRefs = CacheDB().initializeDataset(userID, datasetID, datasetMeta) {
    resolveDatasetFiles(entity.dataFiles, entity.url, entity.docFiles, uploadConfig)
  }

  submitUpload(datasetID, uploadRefs, datasetMeta, uploadConfig)
}
