package vdi.service.plugin.server.context

import java.nio.file.Path
import vdi.db.app.TargetDatabaseDetails
import vdi.model.meta.InstallTargetID
import vdi.service.plugin.script.PluginScript

interface InstallScriptContext<T: Any, C: PluginScript>: ScriptContext<T, C> {
  val installTarget:      InstallTargetID
  val installPath:        Path
  val databaseConfig:     TargetDatabaseDetails
  val dataPropertiesPath: Path?
}

