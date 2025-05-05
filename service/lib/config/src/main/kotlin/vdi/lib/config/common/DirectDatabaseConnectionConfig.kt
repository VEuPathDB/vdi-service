package vdi.lib.config.common

class DirectDatabaseConnectionConfig(
  username: String,
  password: SecretString,
  platform: String?,
  poolSize: UByte?,
  val host: HostAddress,
  val dbName: String,
): DatabaseConnectionConfig(username, password, platform, poolSize)
