package vdi.service.plugin.process.install.data

import java.nio.file.Path
import vdi.db.app.TargetDatabaseDetails
import vdi.io.plugin.requests.InstallDataRequest
import vdi.model.meta.DatasetMetadata
import vdi.service.plugin.process.install.data.CheckCompatibilityScript
import vdi.service.plugin.process.install.data.InstallDataScript
import vdi.service.plugin.process.install.meta.InstallMetaScript
import vdi.service.plugin.server.context.InstallScriptContext

class InstallDataContext(
  override val workspace: Path,
  override val customPath: String,
  override val request: InstallDataRequest,
  override val installPath: Path,
  override val databaseConfig: TargetDatabaseDetails,
  val payload: Path,
  val metadata: DatasetMetadata,
  val metaConfig: InstallMetaScript,
  val dataConfig: InstallDataScript,
  val compatConfig: CheckCompatibilityScript,
): InstallScriptContext<InstallDataRequest> {
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