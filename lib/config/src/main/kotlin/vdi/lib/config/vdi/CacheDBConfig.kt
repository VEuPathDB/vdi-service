package vdi.lib.config.vdi

import org.veupathdb.vdi.lib.common.field.SecretString
import vdi.lib.config.common.HostAddress

data class CacheDBConfig(
  val server: HostAddress,
  val username: String,
  val password: SecretString,
  val name: String?,
  val poolSize: UByte?,
)
