package vdi.lib.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vdi.lib.config.common.HostAddress
import vdi.lib.config.common.HostAddressListDeserializer

data class LDAPConfig(
  @JsonDeserialize(using = HostAddressListDeserializer::class)
  val servers: List<HostAddress>,
  @JsonProperty("baseDn")
  val baseDN: String,
)

