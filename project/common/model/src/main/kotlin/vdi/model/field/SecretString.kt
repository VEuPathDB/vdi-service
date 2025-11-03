package vdi.model.field

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class SecretString @JsonCreator constructor(val asString: String) {
  @JsonValue
  override fun toString() = "***"
}
