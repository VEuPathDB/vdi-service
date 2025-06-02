package vdi.lib.plugin.registry

import vdi.model.data.InstallTargetID

data class PluginDetails(
  val displayName:    String,
  val projects:       List<InstallTargetID>,
  val changesEnabled: Boolean,
) {
  @Suppress("NOTHING_TO_INLINE")
  inline fun appliesTo(project: InstallTargetID) =
    projects.isEmpty() || projects.contains(project)
}
