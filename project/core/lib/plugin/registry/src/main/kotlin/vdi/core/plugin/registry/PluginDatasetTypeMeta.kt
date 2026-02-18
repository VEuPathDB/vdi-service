package vdi.core.plugin.registry

import vdi.model.meta.InstallTargetID

class PluginDatasetTypeMeta(
  val plugin: String,
  val category: String,
  val maxFileSize: ULong,
  val allowedFileExtensions: Array<String>,
  val typeChangesEnabled: Boolean,
  val usesDataPropertiesFiles: Boolean,
  val installTargets: Array<InstallTargetID>?,
) {
  fun appliesTo(target: InstallTargetID) =
    installTargets?.contains(target) ?: true
}