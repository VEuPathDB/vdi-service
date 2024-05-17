package vdi.component.plugin.client

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID

class PluginRequestException(
  val request: String,
  val plugin: String,
  val projectID: ProjectID,
  val datasetID: DatasetID,
  cause: Throwable,
) : Exception(makeMessage(request, plugin, projectID, datasetID, cause), cause)

private fun makeMessage(
  request: String,
  plugin: String,
  projectID: ProjectID,
  datasetID: DatasetID,
  cause: Throwable
) =
  "error while making a(n) $request request to plugin $plugin for dataset $datasetID targeting project $projectID: " +
    cause.message