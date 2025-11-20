package vdi.config.raw.vdi

class DataTypeConfig(
  val name: String,
  val category: String,
  val version: String,
  val maxFileSize: ULong = ULong.MAX_VALUE,
  val allowedFileExtensions: Array<String> = emptyArray(),
  val usesVarMapping: Boolean = false,
)
