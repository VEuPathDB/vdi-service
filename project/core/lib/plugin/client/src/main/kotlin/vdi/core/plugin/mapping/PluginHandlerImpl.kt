package vdi.core.plugin.mapping

import vdi.model.meta.InstallTargetID
import vdi.core.plugin.client.PluginHandlerClient
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.DatasetType

internal class PluginHandlerImpl(
  override val type: DatasetType,
  override val client: PluginHandlerClient,
) : PluginHandler {
  override val name: String
    get() = PluginRegistry.require(type).plugin

  override fun appliesToProject(installTarget: InstallTargetID) =
    PluginRegistry.require(type).appliesTo(installTarget)

  override fun projects() =
    PluginRegistry.require(type).installTargets
      ?.takeUnless { it.isEmpty() }
      ?.asList()
      ?: listOf("*")
}
