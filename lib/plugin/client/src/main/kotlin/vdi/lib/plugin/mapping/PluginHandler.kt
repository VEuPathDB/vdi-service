package vdi.lib.plugin.mapping

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.lib.plugin.client.PluginHandlerClient

/**
 * Plugin Handler
 *
 * Represents a plugin handler service and client.
 */
interface PluginHandler {

  /**
   * Name of the type of dataset that this plugin handler handles.
   */
  val type: DataType

  /**
   * Display name of the type of dataset that this plugin handler handles.
   */
  val displayName: String

  /**
   * The plugin handler API client for connecting to and performing actions on
   * the target plugin handler service.
   */
  val client: PluginHandlerClient

  /**
   * Tests whether this [PluginHandler] instance applies to the target
   * [ProjectID].
   *
   * If a plugin handler does not apply to a target project, then attempting to
   * perform plugin actions via the API is guaranteed to fail.
   *
   * @param projectID ID of the target project to test compatibility with.
   *
   * @return `true` if this [PluginHandler] applies to the given project,
   * otherwise `false`.
   */
  fun appliesToProject(projectID: ProjectID): Boolean

  fun projects(): Collection<String>
}
