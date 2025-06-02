package vdi.service.plugin.server.context

import java.nio.file.Path
import vdi.model.data.DatasetID

sealed interface ScriptContext<T: Any> {
  val datasetID: DatasetID
  val workspace: Path
  val customPath: String
  val request: T
}

