package vdi.service.plugin.process.install.data

import java.nio.file.Path
import vdi.db.app.TargetDatabaseDetails
import vdi.io.plugin.requests.InstallDataRequest
import vdi.logging.logger
import vdi.logging.mark
import vdi.model.meta.DatasetMetadata
import vdi.service.plugin.process.install.meta.InstallMetaScript
import vdi.service.plugin.server.context.InstallScriptContext

internal class InstallDataContext(
  pluginName: String,
  override val workspace: Path,
  override val customPath: String,
  override val request: InstallDataRequest,
  override val installPath: Path,
  override val databaseConfig: TargetDatabaseDetails,
  override val dataPropertiesPath: Path,
  val payload: Path,
  val metadata: DatasetMetadata,
  val metaConfig: InstallMetaScript,
  val dataConfig: InstallDataScript,
  val compatConfig: CheckCompatibilityScript,
): InstallScriptContext<InstallDataRequest, InstallDataScript> {
  override val logger = logger<InstallDataHandler>()
    .mark(
      eventID       = eventID,
      datasetID     = datasetID,
      ownerID       = ownerID,
      scriptName    = scriptConfig.kind.name,
      dataType      = metadata.type,
      pluginName    = pluginName,
      installTarget = installTarget,
    )

  override val scriptConfig: InstallDataScript
    get() = dataConfig

  inline val ownerID
    get() = metadata.owner

  override val eventID
    get() = request.eventID

  override val datasetID
    get() = request.vdiID

  override val installTarget
    get() = request.installTarget

  override fun toString() = "InstallDataContext(ownerID: $ownerID, datasetID: $datasetID, projectID: $installTarget)"
}