package vdi.service.plugin.server.context

import java.nio.file.Path
import vdi.db.app.TargetDatabaseDetails
import vdi.model.api.internal.InstallMetaRequest
import vdi.service.plugin.conf.ScriptConfiguration

data class InstallMetaContext(
  override val workspace: Path,
  override val customPath: String,
  override val request: InstallMetaRequest,
  override val installPath: Path,
  override val databaseConfig: TargetDatabaseDetails,
  val scriptConfig: ScriptConfiguration,
): InstallScriptContext<InstallMetaRequest> {
  inline val ownerID
    get() = metadata.owner

  inline val metadata
    get() = request.meta

  override val datasetID
    get() = request.vdiID

  override val installTarget
    get() = request.installTarget

  override fun toString() = "InstallMetaContext(ownerID: $ownerID, datasetID: $datasetID, projectID: $installTarget)"
}