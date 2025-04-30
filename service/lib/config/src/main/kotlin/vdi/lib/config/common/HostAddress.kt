package vdi.lib.config.common

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(using = HostAddressDeserializer::class)
data class HostAddress(
  val host: String,
  val port: UShort?,
)
