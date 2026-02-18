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
  fileName:  String,
  download:  Boolean,
  forAdmin:  Boolean,
): GetResponse {
  val dataset = ((
    if (forAdmin) CacheDB().selectDataset(datasetID)
    else CacheDB().selectDatasetForUser(userID, datasetID)
  ) ?: return Respond404())
    .also { if (it.isDeleted) return Respond410() }

  (PluginRegistry[dataset.type]
    ?: return Respond400("data type ${dataset.type} is not currently enabled"))
    .also { meta -> if (!meta.usesDataPropertiesFiles) return Respond404() }

  val stream = DatasetStore.getVariablePropertiesFile(dataset.ownerID, datasetID, fileName)?.stream
    ?: return Respond404()

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
  fileName:  String,
  file:      File,
  forAdmin:  Boolean,
): PutResponse {
  val dataset = (CacheDB().selectDataset(datasetID) ?: return Respond404())
    .also { if (it.isDeleted) return Respond410() }
    .takeIf { forAdmin || it.ownerID == userID }
    ?: return Respond403("cannot modify unowned dataset")

  val dtConfig = PluginRegistry[dataset.type]
    ?: return Respond400("data type ${dataset.type} is not currently enabled")

  if (!dtConfig.usesDataPropertiesFiles)
    return Respond403("dataset type does not permit additional data properties")

  DatasetStore.putVariablePropertiesFile(dataset.ownerID, datasetID, fileName, file::inputStream)

  return PutResponse.respond202()
}