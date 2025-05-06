package vdi.lib.config.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.common.field.SecretString

class LDAPDatabaseConnectionConfig(
  username: String,
  password: SecretString,
  poolSize: UByte?,
  schema: String?,
  val platform: String?,
  @JsonProperty("lookupCn")
  val lookupCN: String,
): DatabaseConnectionConfig(username, password, poolSize, schema)
