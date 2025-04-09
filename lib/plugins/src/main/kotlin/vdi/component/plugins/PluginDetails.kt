package vdi.component.plugins

import org.veupathdb.vdi.lib.common.field.ProjectID

data class PluginDetails(val displayName: String, val projects: Set<ProjectID>) {
  @Suppress("NOTHING_TO_INLINE")
  inline fun appliesTo(project: ProjectID) =
    projects.isEmpty() || projects.contains(project)
}
