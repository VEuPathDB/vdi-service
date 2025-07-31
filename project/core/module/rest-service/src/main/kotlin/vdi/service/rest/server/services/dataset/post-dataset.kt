@file:JvmName("DatasetPostService")
package vdi.service.rest.server.services.dataset

import java.io.File
import java.net.URI
import vdi.core.db.cache.CacheDB
import vdi.model.data.DatasetID
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.gen.model.BadRequestError
import vdi.service.rest.generated.model.DatasetPostRequestBody
import vdi.service.rest.generated.model.DatasetProxyPostRequestBody
import vdi.service.rest.gen.model.support.UnionCreateDatasetResponse
import vdi.service.rest.gen.model.DatasetPostMeta
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.inputs.toDatasetMeta

fun <T: ControllerBase> T.createDataset(datasetID: DatasetID, entity: DatasetPostRequestBody, uploadConfig: UploadConfig) {
  val datasetMeta = entity.toDatasetMeta(userID)
  val uploadRefs = CacheDB().initializeDataset(userID, datasetID, datasetMeta) {
    resolveDatasetFiles(entity.dataFiles, entity.url, entity.docFiles, uploadConfig)
  }

  submitUpload(datasetID, uploadRefs, datasetMeta, uploadConfig)
}

//fun <T: ControllerBase> T.createDataset(
//  datasetID: DatasetID,
//  details: DatasetPostMeta,
//  metaFiles: List<File>?,
//  dataFiles: List<File>?,
//  url: URI?,
//): UnionCreateDatasetResponse {
//  if (dataFiles == null && url == null) {
//    logger.debug("bad request: no data file or url provided")
//    return UnionCreateDatasetResponse.json400(BadRequestError("must provide either data files or a download source url"))
//  }
//}

fun <T: ControllerBase> T.createDataset(datasetID: DatasetID, entity: DatasetProxyPostRequestBody, uploadConfig: UploadConfig) {
  val datasetMeta = entity.toDatasetMeta(userID)
  val uploadRefs = CacheDB().initializeDataset(userID, datasetID, datasetMeta) {
    resolveDatasetFiles(entity.dataFiles, entity.url, entity.docFiles, uploadConfig)
  }

  submitUpload(datasetID, uploadRefs, datasetMeta, uploadConfig)
}
