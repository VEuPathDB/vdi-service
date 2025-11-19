package vdi.service.plugin.process.preprocess

import java.nio.file.Path
import vdi.io.plugin.requests.ImportRequest
import vdi.model.EventID
import vdi.model.meta.DatasetID
import vdi.service.plugin.server.context.ScriptContext

data class ImportContext(
  override val workspace: Path,
  override val customPath: String,
  override val request: ImportRequest,
  val importReadyZip: Path,
  val dataDictPath: Path,
  val scriptConfig: ImportScript,
): ScriptContext<ImportRequest> {
  inline val metadata
    get() = request.meta

  inline val ownerID
    get() = metadata.owner

  override val eventID: EventID
    get() = request.eventID

  override val datasetID: DatasetID
    get() = request.vdiID

  override fun toString() = "ImportContext(ownerID: ${ownerID}, datasetID: $datasetID)"
}