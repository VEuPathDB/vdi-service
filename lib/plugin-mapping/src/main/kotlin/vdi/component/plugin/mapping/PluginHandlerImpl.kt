package vdi.component.plugin.mapping

import org.veupathdb.vdi.lib.common.field.ProjectID

internal class PluginHandlerImpl(
  override val type: String,
  override val displayName: String,
  override val client: vdi.component.plugin.client.PluginHandlerClient,
  private val projects: Set<ProjectID>,
) : PluginHandler {
  override fun appliesToProject(projectID: ProjectID): Boolean {
    return projects.isEmpty() || projects.contains(projectID)
  }

  override fun projects(): List<String> {
    return projects.toList()
  }
}