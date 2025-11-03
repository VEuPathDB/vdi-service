package vdi.service.plugin.server.context

import java.nio.file.Path
import vdi.model.api.internal.ImportRequest
import vdi.model.data.DatasetID
import vdi.service.plugin.conf.ScriptConfiguration

data class ImportContext(
  override val workspace: Path,
  override val customPath: String,
  override val request: ImportRequest,
  val payload: Path,
  val scriptConfig: ScriptConfiguration,
): ScriptContext<ImportRequest> {
  inline val metadata
    get() = request.meta

  inline val ownerID
    get() = metadata.owner

  override val datasetID: DatasetID
    get() = request.vdiID

  override fun toString() = "ImportContext(ownerID: ${ownerID}, datasetID: $datasetID)"
}