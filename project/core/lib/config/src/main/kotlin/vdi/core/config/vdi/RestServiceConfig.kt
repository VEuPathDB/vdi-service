package vdi.core.config.vdi

data class RestServiceConfig(
  val maxUploadSize: ULong = 1073741824uL,
  val userMaxStorageSize: ULong = 10737418240uL,
  val enableJerseyTrace: Boolean = false,
)
