package vdi.lib.config.vdi

import org.veupathdb.vdi.lib.common.field.SecretString
import vdi.lib.config.common.HostAddress

data class CacheDBConfig(
  val host: HostAddress,
  val user: String,
  val pass: SecretString,
  val name: String?,
  val poolSize: UByte?,
)
