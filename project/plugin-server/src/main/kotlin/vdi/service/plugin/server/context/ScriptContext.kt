package vdi.service.plugin.server.context

import java.nio.file.Path
import vdi.model.EventID
import vdi.model.meta.DatasetID

interface ScriptContext<T: Any> {
  val eventID: EventID
  val datasetID: DatasetID
  val workspace: Path
  val customPath: String
  val request: T
}

