package vdi.service.rest.config

import vdi.core.config.vdi.RestServiceConfig

data class UploadConfig(val maxUploadSize: ULong, val userMaxStorageSize: ULong) {
  constructor(config: RestServiceConfig?): this(
    maxUploadSize      = config?.maxUploadSize ?: MaxUploadSize,
    userMaxStorageSize = config?.userMaxStorageSize ?: MaxStorageSize,
  )

  private companion object {
    inline val MaxUploadSize get() = 1073741824uL
    inline val MaxStorageSize get() = 10737418240uL
  }
}
