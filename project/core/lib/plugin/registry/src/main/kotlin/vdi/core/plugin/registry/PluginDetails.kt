package vdi.core.plugin.registry

import vdi.model.data.InstallTargetID

data class PluginDetails(
  val name:           String,
  val projects:       List<InstallTargetID>,
  val changesEnabled: Boolean,
  val maxFileSize:    ULong,
) {
  @Suppress("NOTHING_TO_INLINE")
  inline fun appliesTo(project: InstallTargetID) =
    projects.isEmpty() || projects.contains(project)
}
