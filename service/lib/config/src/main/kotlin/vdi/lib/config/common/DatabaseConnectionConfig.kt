package vdi.lib.config.common

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(using = DatabaseConnectionConfigDeserializer::class)
sealed class DatabaseConnectionConfig(
  val username: String,
  val password: SecretString,
  val platform: String?,
  val poolSize: UByte?,
)
