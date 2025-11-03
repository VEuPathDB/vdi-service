package vdi.core.plugin.mapping

import vdi.model.data.InstallTargetID
import vdi.core.plugin.client.PluginHandlerClient
import vdi.core.plugin.registry.PluginDetails
import vdi.model.data.DatasetType

internal class PluginHandlerImpl(
  override val type: DatasetType,
  override val client: PluginHandlerClient,
  private val details: PluginDetails
) : PluginHandler {
  override val name: String
    get() = details.name

  override fun appliesToProject(installTarget: InstallTargetID) =
    details.appliesTo(installTarget)

  override fun projects() =
    details.projects.ifEmpty { listOf("*") }
}
