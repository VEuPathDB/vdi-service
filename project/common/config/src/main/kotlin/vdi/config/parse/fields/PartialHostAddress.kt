package vdi.config.parse.fields

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vdi.config.parse.serde.HostAddressDeserializer
import vdi.model.field.HostAddress

@JsonDeserialize(using = HostAddressDeserializer::class)
data class PartialHostAddress(val host: String, val port: UShort?) {
  fun toHostAddress(fallbackPort: UShort) =
    HostAddress(host, port ?: fallbackPort)
}
