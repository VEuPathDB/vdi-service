package org.veupathdb.vdi.lib.handler.mapping

import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClient

internal class PluginHandlerImpl(
  override val type: String,
  override val displayName: String,
  override val client: PluginHandlerClient,
  private val projects: Set<ProjectID>
) : PluginHandler {
  override fun appliesToProject(projectID: ProjectID): Boolean {
    return projects.isEmpty() || projects.contains(projectID)
  }
}