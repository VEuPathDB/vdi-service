package vdi.config.raw.db

import kotlin.time.Duration
import vdi.config.parse.fields.PartialHostAddress
import vdi.model.field.SecretString


class DirectDatabaseConnectionConfig(
  override val username: String,
  override val password: SecretString,
  override val poolSize: UByte?,
  override val schema: String?,
  override val idleTimeout: Duration?,
  val platform: String,
  val server: PartialHostAddress,
  val dbName: String,
): DatabaseConnectionConfig
