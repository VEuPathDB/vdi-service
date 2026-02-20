package vdi.config.raw.db

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import kotlin.time.Duration
import vdi.config.raw.db.serde.DatabaseConnectionConfigDeserializer
import vdi.model.field.SecretString

@JsonDeserialize(using = DatabaseConnectionConfigDeserializer::class)
sealed interface DatabaseConnectionConfig {
  val username: String
  val password: SecretString
  val poolSize: UByte?
  val schema: String?
  val idleTimeout: Duration?
}
