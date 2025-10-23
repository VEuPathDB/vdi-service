package vdi.core.plugin.registry

class PluginDatasetTypeMeta(
  val category: String,
  val maxFileSize: ULong,
  val allowedFileExtensions: Array<String>,
)