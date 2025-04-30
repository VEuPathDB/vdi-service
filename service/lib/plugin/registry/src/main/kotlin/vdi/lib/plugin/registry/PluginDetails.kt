package vdi.lib.plugin.registry

import org.veupathdb.vdi.lib.common.field.ProjectID

data class PluginDetails(
  val displayName:    String,
  val projects:       List<ProjectID>,
  val changesEnabled: Boolean,
) {
  @Suppress("NOTHING_TO_INLINE")
  inline fun appliesTo(project: ProjectID) =
    projects.isEmpty() || projects.contains(project)
}
