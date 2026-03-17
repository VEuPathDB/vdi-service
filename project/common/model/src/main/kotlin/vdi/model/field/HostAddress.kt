package vdi.model.field

import com.fasterxml.jackson.annotation.JsonIgnore

data class HostAddress(val host: String, val port: UShort) {
  @JsonIgnore
  fun getPortAsInt() = port.toInt()

  override fun toString() = "$host:$port"
}
