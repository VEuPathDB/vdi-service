package vdi.core.plugin.mapping

import vdi.model.data.InstallTargetID
import vdi.core.plugin.client.PluginHandlerClient
import vdi.model.data.DatasetType

/**
 * Plugin Handler
 *
 * Represents a plugin handler service and client.
 */
interface PluginHandler {

  /**
   * Name of the type of dataset that this plugin handler handles.
   */
  val type: DatasetType

  /**
   * Display name of the type of dataset that this plugin handler handles.
   */
  val name: String

  /**
   * The plugin handler API client for connecting to and performing actions on
   * the target plugin handler service.
   */
  val client: PluginHandlerClient

  /**
   * Tests whether this [PluginHandler] instance applies to the target
   * [InstallTargetID].
   *
   * If a plugin handler does not apply to a target project, then attempting to
   * perform plugin actions via the API is guaranteed to fail.
   *
   * @param installTarget ID of the target project to test compatibility with.
   *
   * @return `true` if this [PluginHandler] applies to the given project,
   * otherwise `false`.
   */
  fun appliesToProject(installTarget: InstallTargetID): Boolean

  fun projects(): Collection<String>
}
