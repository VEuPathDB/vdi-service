package vdi.core.plugin.mapping

import vdi.model.data.DataType
import vdi.model.data.InstallTargetID
import vdi.lib.plugin.client.PluginHandlerClient
import vdi.lib.plugin.registry.PluginDetails

internal class PluginHandlerImpl(
  override val type: DataType,
  override val client: PluginHandlerClient,
  private val details: PluginDetails
) : PluginHandler {
  override val displayName: String
    get() = details.displayName

  override fun appliesToProject(installTarget: InstallTargetID) =
    details.appliesTo(installTarget)

  override fun projects() =
    details.projects.ifEmpty { listOf("*") }
}
