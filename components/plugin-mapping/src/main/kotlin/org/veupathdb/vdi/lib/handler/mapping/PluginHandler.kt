package org.veupathdb.vdi.lib.handler.mapping

import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClient

/**
 * Plugin Handler
 *
 * Represents a plugin handler service and client.
 */
interface PluginHandler {

  /**
   * Name of the type of dataset that this plugin handler handles.
   */
  val type: String

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
   * Path in the plugin container to the user dataset files root directory/mount
   * point.  User dataset files are installed to a subdirectory under this path.
   */
  val userFilesRoot: String

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
}