package vdi.service.plugin.server.context

import java.nio.file.Path
import vdi.lib.db.app.TargetDatabaseDetails
import vdi.model.data.InstallTargetID

sealed interface InstallScriptContext<T: Any>: ScriptContext<T> {
  val installTarget: InstallTargetID
  val installPath: Path
  val databaseConfig: TargetDatabaseDetails
}

