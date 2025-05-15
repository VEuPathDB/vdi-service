package vdi.lib.config.common

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.veupathdb.vdi.lib.common.field.SecretString

@JsonDeserialize(using = DatabaseConnectionConfigDeserializer::class)
sealed interface DatabaseConnectionConfig {
  val username: String
  val password: SecretString
  val poolSize: UByte?
  val schema: String?
}
