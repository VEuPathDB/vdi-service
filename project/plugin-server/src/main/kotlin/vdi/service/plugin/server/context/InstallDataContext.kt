package vdi.service.plugin.server.context

import java.nio.file.Path
import vdi.lib.db.app.TargetDatabaseDetails
import vdi.model.api.internal.InstallDataRequest
import vdi.model.data.DatasetMetadata
import vdi.service.plugin.conf.ScriptConfiguration

data class InstallDataContext(
  override val workspace: Path,
  override val customPath: String,
  override val request: InstallDataRequest,
  override val installPath: Path,
  override val databaseConfig: TargetDatabaseDetails,
  val payload: Path,
  val metadata: DatasetMetadata,
  val metaConfig: ScriptConfiguration,
  val dataConfig: ScriptConfiguration,
  val compatConfig: ScriptConfiguration,
): InstallScriptContext<InstallDataRequest> {
  inline val ownerID
    get() = metadata.owner

  override val datasetID
    get() = request.vdiID

  override val installTarget
    get() = request.installTarget

  override fun toString() = "InstallDataContext(ownerID: $ownerID, datasetID: $datasetID, projectID: $installTarget)"
}