package vdi.config.raw.db

import vdi.config.parse.fields.PartialHostAddress
import vdi.model.field.SecretString


class DirectDatabaseConnectionConfig(
  override val username: String,
  override val password: SecretString,
  override val poolSize: UByte?,
  override val schema: String?,
  val platform: String,
  val server: PartialHostAddress,
  val dbName: String,
): DatabaseConnectionConfig
