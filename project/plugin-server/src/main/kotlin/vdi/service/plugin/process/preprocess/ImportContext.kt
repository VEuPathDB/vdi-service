package vdi.service.plugin.process.preprocess

import java.nio.file.Path
import vdi.io.plugin.requests.ImportRequest
import vdi.logging.logger
import vdi.logging.mark
import vdi.model.EventID
import vdi.model.meta.DatasetID
import vdi.service.plugin.server.context.ScriptContext

internal class ImportContext(
  pluginName: String,
  override val workspace: Path,
  override val customPath: String,
  override val request: ImportRequest,
  override val scriptConfig: ImportScript,
  val importReadyZip: Path,
): ScriptContext<ImportRequest, ImportScript> {
  override val logger = logger<ImportHandler>()
    .mark(
      eventID    = eventID,
      datasetID  = datasetID,
      ownerID    = ownerID,
      scriptName = scriptConfig.kind.name,
      dataType   = metadata.type,
      pluginName = pluginName,
    )

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