package vdi.service.plugin.server.context

import java.nio.file.Path
import vdi.model.EventID
import vdi.model.meta.DatasetID
import vdi.service.plugin.script.PluginScript

interface ScriptContext<T: Any, C: PluginScript> {
  val eventID:      EventID
  val datasetID:    DatasetID
  val workspace:    Path
  val customPath:   String
  val request:      T
  val scriptConfig: C
}

