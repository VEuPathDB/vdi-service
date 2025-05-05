package vdi.lib.config.common

import com.fasterxml.jackson.annotation.JsonProperty

class LDAPDatabaseConnectionConfig(
  username: String,
  password: SecretString,
  platform: String?,
  poolSize: UByte?,
  @JsonProperty("lookupCn")
  val lookupCN: String,
): DatabaseConnectionConfig(username, password, platform, poolSize)
