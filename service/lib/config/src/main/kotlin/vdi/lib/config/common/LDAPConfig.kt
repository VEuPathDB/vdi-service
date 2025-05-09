package vdi.lib.config.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class LDAPConfig(
  @JsonDeserialize(using = HostAddressListDeserializer::class)
  val servers: List<HostAddress>,
  @param:JsonProperty("baseDn")
  @field:JsonProperty("baseDn")
  val baseDN: String,
)

