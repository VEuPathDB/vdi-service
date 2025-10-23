package vdi.config.raw.vdi

data class DataTypeConfig(
  val name: String,
  val category: String,
  val version: String,
  val maxFileSize: ULong = ULong.MAX_VALUE,
)
