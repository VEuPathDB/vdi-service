package vdi.component.db.cache

import vdi.components.common.fields.SecretString

data class CacheDBConfig(
  val host: String,
  val port: UShort,
  val name: String,
  val username: SecretString,
  val password: SecretString,
  val poolSize: UByte,
)