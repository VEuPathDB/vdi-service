package vdi.service.plugin.server.context

import java.nio.file.Path
import vdi.lib.db.app.TargetDatabaseDetails
import vdi.model.api.internal.UninstallRequest
import vdi.service.plugin.conf.ScriptConfiguration

data class UninstallDataContext(
  override val workspace: Path,
  override val customPath: String,
  override val request: UninstallRequest,
  override val installPath: Path,
  override val databaseConfig: TargetDatabaseDetails,
  val scriptConfig: ScriptConfiguration,
): InstallScriptContext<UninstallRequest> {
  override val datasetID
    get() = request.vdiID

  override val installTarget
    get() = request.installTarget

  override fun toString() = "UninstallContext(datasetID: $datasetID, projectID: $installTarget)"
}