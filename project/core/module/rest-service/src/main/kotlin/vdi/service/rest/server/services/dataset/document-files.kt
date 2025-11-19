package vdi.service.rest.server.services.dataset

import java.io.File
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetRecord
import vdi.model.meta.DatasetID
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles.GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse as GetResponse
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles.PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse as PutResponse

fun getUserDocumentForAdmin(datasetID: DatasetID, filename: String) =
  CacheDB().selectDataset(datasetID).getDocument(filename)

fun ControllerBase.getUserDocumentForUser(datasetID: DatasetID, filename: String) =
  lookupVisibleDataset(userID, datasetID).getDocument(filename)

private fun DatasetRecord?.getDocument(filename: String) =
  this?.let { DatasetStore.getDocumentFile(ownerID, datasetID, filename) }
    ?.let { GetResponse.respond200WithApplicationOctetStream({ o -> it.stream.use { i -> i.transferTo(o) } }, GetResponse.headersFor200().withContentDisposition("attachment; filename=\"$filename\"")) }
    ?: Static404.wrap()

fun ControllerBase.putUserDocument(datasetID: DatasetID, filename: String, tmpFile: File?) =
  tmpFile?.run {
    takeUnless { CacheDB().selectDatasetForUser(userID, datasetID) == null }
      ?.let { DatasetStore.putDocumentFile(userID, datasetID, filename, it::inputStream) }
      ?.let { PutResponse.respond204() }
      ?: Static404.wrap()
  }
    ?: PutResponse.respond400WithApplicationJson(BadRequestError("empty request body"))!!
