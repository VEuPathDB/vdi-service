package vdi.config.raw.vdi

data class RestServiceConfig(
  val maxUploadSize: ULong?,
  val userMaxStorageSize: ULong?,
  val enableJerseyTrace: Boolean?,
)
