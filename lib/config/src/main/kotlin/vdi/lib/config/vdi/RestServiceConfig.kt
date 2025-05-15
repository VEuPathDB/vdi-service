package vdi.lib.config.vdi

data class RestServiceConfig(
  val maxUploadSize: ULong?,
  val userMaxStorageSize: ULong?,
  val enableJerseyTrace: Boolean?,
)
