package vdi.core.plugin.registry

import vdi.model.meta.InstallTargetID

data class PluginDetails(
  val name:     String,
  val projects: List<InstallTargetID>,
) {
  @Suppress("NOTHING_TO_INLINE")
  inline fun appliesTo(project: InstallTargetID) =
    projects.isEmpty() || projects.contains(project)
}
