package vdi.lib.db.app

import vdi.model.field.HostAddress
import vdi.model.field.SecretString

data class TargetDatabaseDetails(
  val name: String,
  val server: HostAddress,
  val user: String,
  val pass: SecretString,
  val schema: String,
  val poolSize: UByte,
  val platform: TargetDBPlatform,
)

