package vdi.lib.config.common

import org.veupathdb.vdi.lib.common.field.SecretString

class DirectDatabaseConnectionConfig(
  username: String,
  password: SecretString,
  poolSize: UByte?,
  schema: String?,
  val platform: String,
  val server: HostAddress,
  val dbName: String,
): DatabaseConnectionConfig(username, password, poolSize, schema)
