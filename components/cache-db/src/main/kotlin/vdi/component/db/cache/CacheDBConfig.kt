package vdi.component.db.cache

import org.veupathdb.vdi.lib.common.field.SecretString

data class CacheDBConfig(
  val host: String,
  val port: UShort,
  val name: String,
  val username: SecretString,
  val password: SecretString,
  val poolSize: UByte,
)