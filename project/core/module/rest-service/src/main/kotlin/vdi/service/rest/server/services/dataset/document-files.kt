package vdi.service.rest.server.services.dataset

import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetRecord
import vdi.model.data.DatasetID
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles.GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse as Response

fun getUserDocumentForAdmin(datasetID: DatasetID, filename: String): Response =
  CacheDB().selectDataset(datasetID).getDocument(filename)

fun ControllerBase.getUserDocumentForUser(datasetID: DatasetID, filename: String): Response =
  lookupVisibleDataset(userID, datasetID).getDocument(filename)

private fun DatasetRecord?.getDocument(filename: String) =
  this?.let { DatasetStore.getDocumentFile(ownerID, datasetID, filename) }
    ?.let { Response.respond200WithApplicationOctetStream({ o -> it.stream.use { i -> i.transferTo(o) } }, Response.headersFor200().withContentDisposition("attachment; filename=\"$filename\"")) }
    ?: Static404.wrap()
