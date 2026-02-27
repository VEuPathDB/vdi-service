package vdi.core.config.vdi

import com.fasterxml.jackson.annotation.JsonIgnore

data class RestServiceConfig(
  val maxUploadSize: ULong = 1073741824uL,
  val userMaxStorageSize: ULong = 10737418240uL,
  val enableJerseyTrace: Boolean = false,
) {
  @get:JsonIgnore
  val longMaxUploadSize: Long get() = maxUploadSize.toLong()

  @get:JsonIgnore
  val longUserMaxStorageSize: Long get() = userMaxStorageSize.toLong()
}
