package vdi.lib.config.common

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.veupathdb.vdi.lib.common.util.HostAddress

@JsonDeserialize(using = HostAddressDeserializer::class)
data class HostAddress(val host: String, val port: UShort?) {
  fun toCommonType(fallbackPort: UShort) =
    HostAddress(host, port ?: fallbackPort)
}
