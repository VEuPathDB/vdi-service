package vdi.model.field

import com.fasterxml.jackson.annotation.JsonValue

data class SecretString(val asString: String) {
  @JsonValue
  override fun toString() = "***"
}
