package vdi.service.plugin.process.install.meta

import java.nio.file.Path
import vdi.db.app.TargetDatabaseDetails
import vdi.io.plugin.requests.InstallMetaRequest
import vdi.model.EventID
import vdi.service.plugin.server.context.InstallScriptContext

data class InstallMetaContext(
  override val workspace:          Path,
  override val customPath:         String,
  override val request:            InstallMetaRequest,
  override val installPath:        Path,
  override val dataPropertiesPath: Path?,
  override val databaseConfig:     TargetDatabaseDetails,
  override val scriptConfig:       InstallMetaScript,
): InstallScriptContext<InstallMetaRequest, InstallMetaScript> {
  inline val ownerID
    get() = metadata.owner

  inline val metadata
    get() = request.meta

  override val eventID: EventID
    get() = request.eventID

  override val datasetID
    get() = request.vdiID

  override val installTarget
    get() = request.installTarget

  override fun toString() = "InstallMetaContext(ownerID: $ownerID, datasetID: $datasetID, projectID: $installTarget)"
}