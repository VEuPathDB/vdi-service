package vdi.service.plugin.server.context

import java.nio.file.Path
import vdi.db.app.TargetDatabaseDetails
import vdi.model.meta.InstallTargetID

interface InstallScriptContext<T: Any>: ScriptContext<T> {
  val installTarget: InstallTargetID
  val installPath: Path
  val databaseConfig: TargetDatabaseDetails
}

