package vdi.config.raw.db

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.time.Duration
import vdi.model.field.SecretString

class LDAPDatabaseConnectionConfig(
  override val username: String,
  override val password: SecretString,
  override val poolSize: UByte?,
  override val schema: String?,
  override val idleTimeout: Duration?,
  @param:JsonProperty("lookupCn")
  @field:JsonProperty("lookupCn")
  val lookupCN: String,
): DatabaseConnectionConfig
