package vdi.core.plugin.registry

class PluginDatasetTypeMeta(
  val category: String,
  val maxFileSize: ULong,
  val allowedFileExtensions: Array<String>,
  val usesDataPropertiesFiles: Boolean,
  val varPropertiesFileNameSingular: String = "Variable Properties File",
  val varPropertiesFileNamePlural: String = "Variable Properties Files",
)