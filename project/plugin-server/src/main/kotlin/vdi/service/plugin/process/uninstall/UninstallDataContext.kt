package vdi.service.plugin.process.uninstall

import java.nio.file.Path
import vdi.db.app.TargetDatabaseDetails
import vdi.io.plugin.requests.UninstallRequest
import vdi.model.EventID
import vdi.service.plugin.server.context.InstallScriptContext

class UninstallDataContext(
  override val workspace: Path,
  override val customPath: String,
  override val request: UninstallRequest,
  override val installPath: Path,
  override val databaseConfig: TargetDatabaseDetails,
  val scriptConfig: UninstallScript,
): InstallScriptContext<UninstallRequest> {
  override val eventID: EventID
    get() = request.eventID

  override val datasetID
    get() = request.vdiID

  override val installTarget
    get() = request.installTarget

  override fun toString() = "UninstallContext(datasetID: $datasetID, projectID: $installTarget)"
}