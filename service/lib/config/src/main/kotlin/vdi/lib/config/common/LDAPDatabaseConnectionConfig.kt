package vdi.lib.config.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.common.field.SecretString

class LDAPDatabaseConnectionConfig(
  override val username: String,
  override val password: SecretString,
  override val poolSize: UByte?,
  override val schema: String?,
  @param:JsonProperty("lookupCn")
  @field:JsonProperty("lookupCn")
  val lookupCN: String,
): DatabaseConnectionConfig
