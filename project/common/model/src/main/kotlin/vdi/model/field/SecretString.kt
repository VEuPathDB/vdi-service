package vdi.model.field

import com.fasterxml.jackson.annotation.JsonIgnore

data class SecretString(@JsonIgnore val asString: String) {
  override fun toString() = "***"
}
