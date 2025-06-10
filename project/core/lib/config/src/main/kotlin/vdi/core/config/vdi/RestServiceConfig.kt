package vdi.core.config.vdi

data class RestServiceConfig(
  val maxUploadSize: ULong?,
  val userMaxStorageSize: ULong?,
  val enableJerseyTrace: Boolean?,
)
