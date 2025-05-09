package vdi.lib.config.common

import org.veupathdb.vdi.lib.common.field.SecretString

class DirectDatabaseConnectionConfig(
  override val username: String,
  override val password: SecretString,
  override val poolSize: UByte?,
  override val schema: String?,
  val platform: String,
  val server: HostAddress,
  val dbName: String,
): DatabaseConnectionConfig
