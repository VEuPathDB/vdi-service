package vdi.lib.config.vdi

import vdi.lib.config.common.HostAddress
import vdi.lib.config.common.SecretString

data class CacheDBConfig(
  val host: HostAddress,
  val user: String,
  val pass: SecretString,
  val name: String?,
  val poolSize: UByte?,
)
