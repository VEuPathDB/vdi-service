package vdi.service.plugin.process.uninstall

import java.nio.file.Path
import vdi.db.app.TargetDatabaseDetails
import vdi.io.plugin.requests.UninstallRequest
import vdi.logging.logger
import vdi.logging.mark
import vdi.model.EventID
import vdi.service.plugin.server.context.InstallScriptContext

internal class UninstallDataContext(
  pluginName: String,
  override val workspace: Path,
  override val customPath: String,
  override val request: UninstallRequest,
  override val installPath: Path,
  override val databaseConfig: TargetDatabaseDetails,
  override val scriptConfig: UninstallScript,
): InstallScriptContext<UninstallRequest, UninstallScript> {
  override val logger = logger<UninstallDataHandler>()
    .mark(
      eventID       = eventID,
      datasetID     = datasetID,
      scriptName    = scriptConfig.kind.name,
      pluginName    = pluginName,
      installTarget = installTarget,
    )

  override val eventID: EventID
    get() = request.eventID

  override val datasetID
    get() = request.vdiID

  override val installTarget
    get() = request.installTarget

  override val dataPropertiesPath: Path?
    get() = null

  override fun toString() = "UninstallContext(datasetID: $datasetID, projectID: $installTarget)"
}