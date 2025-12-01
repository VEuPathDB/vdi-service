package vdi.service.rest.server.services.dataset.files

import jakarta.ws.rs.core.StreamingOutput
import java.io.File
import vdi.core.db.cache.CacheDB
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.DatasetID
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.DatasetFiles
import vdi.service.rest.server.outputs.*
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles.GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse as GetResponse
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles.PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse as PutResponse

fun DatasetFiles.getVariablePropertiesFile(
  datasetID: DatasetID,
  fileName: String,
  download: Boolean,
  forAdmin: Boolean,
): GetResponse {
  val dataset = ((
    if (forAdmin) CacheDB().selectDataset(datasetID)
    else CacheDB().selectDatasetForUser(userID, datasetID)
  ) ?: return Static404.wrap())
    .also { if (it.isDeleted) return GoneError().wrap() }

  val stream = DatasetStore.getVariablePropertiesFile(dataset.ownerID, datasetID, fileName)?.stream
    ?: return Static404.wrap()

  return GetResponse.respond200WithTextTabSeparatedValues(
    StreamingOutput { stream.use { file -> file.transferTo(it) } },
    if (download)
      GetResponse.headersFor200().withContentDisposition(makeContentDisposition(fileName))
    else
      GetResponse.headersFor200()
  )
}

fun DatasetFiles.putVariablePropertiesFile(
  datasetID: DatasetID,
  fileName: String,
  file: File,
  forAdmin: Boolean,
): PutResponse {
  val dataset = (CacheDB().selectDataset(datasetID) ?: return Static404.wrap())
    .also { if (it.isDeleted) return GoneError().wrap() }
    .takeIf { forAdmin || it.ownerID == userID }
    ?: return ForbiddenError("cannot modify unowned dataset").wrap()

  val dtConfig = PluginRegistry.configDataOrNullFor(dataset.type)
    ?: return BadRequestError("dataset data type is disabled").wrap()

  if (!dtConfig.usesDataPropertiesFiles)
    return ForbiddenError("dataset type does not permit ${dtConfig.varPropertiesFileNamePlural.lowercase()}").wrap()

  DatasetStore.putVariablePropertiesFile(dataset.ownerID, datasetID, fileName, file::inputStream)

  return PutResponse.respond202()
}