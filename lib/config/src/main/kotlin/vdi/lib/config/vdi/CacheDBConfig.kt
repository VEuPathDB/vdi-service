package vdi.lib.config.vdi

import org.veupathdb.vdi.lib.common.field.SecretString
import org.veupathdb.vdi.lib.config.PartialHostAddress

data class CacheDBConfig(
  val server: PartialHostAddress,
  val username: String,
  val password: SecretString,
  val name: String?,
  val poolSize: UByte?,
)
