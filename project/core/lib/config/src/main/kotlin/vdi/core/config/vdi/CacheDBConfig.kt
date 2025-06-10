package vdi.core.config.vdi

import vdi.config.parse.fields.PartialHostAddress
import vdi.model.field.SecretString

data class CacheDBConfig(
  val server: PartialHostAddress,
  val username: String,
  val password: SecretString,
  val name: String?,
  val poolSize: UByte?,
)
