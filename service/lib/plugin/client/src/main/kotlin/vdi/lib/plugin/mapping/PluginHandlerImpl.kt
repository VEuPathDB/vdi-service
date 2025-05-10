package vdi.lib.plugin.mapping

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.lib.plugin.client.PluginHandlerClient
import vdi.lib.plugin.registry.PluginDetails

internal class PluginHandlerImpl(
  override val type: DataType,
  override val client: PluginHandlerClient,
  private val details: PluginDetails
) : PluginHandler {
  override val displayName: String
    get() = details.displayName

  override fun appliesToProject(projectID: ProjectID) =
    details.appliesTo(projectID)

  override fun projects() =
    details.projects.ifEmpty { listOf("*") }
}
