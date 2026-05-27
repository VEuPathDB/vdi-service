package vdi.service.plugin.server.context

import kotlinx.io.files.Path
import vdi.db.app.TargetDatabaseDetails
import vdi.model.meta.InstallTargetID
import vdi.service.plugin.script.PluginScript

interface InstallScriptContext<T: Any, C: PluginScript>: ScriptContext<T, C> {
  /**
   * Name/identifier of the installation target database/project.
   */
  val installTarget: InstallTargetID

  /**
   * Filesystem path into which dataset files should be installed.
   */
  val installPath: Path

  /**
   * Database connection configuration for the installation target.
   */
  val databaseConfig: TargetDatabaseDetails

  /**
   * Optional path to a directory containing dataset properties files.
   */
  val dataPropertiesPath: Path?
}

