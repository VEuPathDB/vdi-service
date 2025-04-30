package vdi.lib.config.common

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(using = SecretStringDeserializer::class)
data class SecretString(val value: String) {
  override fun toString() = "***"
}
