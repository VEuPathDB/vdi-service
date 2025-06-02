@file:JvmName("DatasetPostService")
package vdi.service.rest.server.services.dataset

import vdi.model.data.DatasetID
import vdi.lib.db.cache.CacheDB
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPostRequestBody
import vdi.service.rest.generated.model.DatasetProxyPostRequestBody
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.inputs.toDatasetMeta

fun <T: ControllerBase> T.createDataset(datasetID: DatasetID, entity: DatasetPostRequestBody, uploadConfig: UploadConfig) {
  val datasetMeta = entity.toDatasetMeta(userID)
  val uploadRefs = CacheDB().initializeDataset(userID, datasetID, datasetMeta) {
    resolveDatasetFiles(entity.dataFiles, entity.url, entity.docFiles, uploadConfig)
  }

  submitUpload(datasetID, uploadRefs, datasetMeta, uploadConfig)
}

fun <T: ControllerBase> T.createDataset(datasetID: DatasetID, entity: DatasetProxyPostRequestBody, uploadConfig: UploadConfig) {
  val datasetMeta = entity.toDatasetMeta(userID)
  val uploadRefs = CacheDB().initializeDataset(userID, datasetID, datasetMeta) {
    resolveDatasetFiles(entity.dataFiles, entity.url, entity.docFiles, uploadConfig)
  }

  submitUpload(datasetID, uploadRefs, datasetMeta, uploadConfig)
}
//