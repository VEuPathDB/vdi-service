package vdi.config.raw.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vdi.config.parse.fields.PartialHostAddress
import vdi.config.parse.serde.HostAddressListDeserializer

data class LDAPConfig(
  @JsonDeserialize(using = HostAddressListDeserializer::class)
  val servers: List<PartialHostAddress>,
  @param:JsonProperty("baseDn")
  @field:JsonProperty("baseDn")
  val baseDN: String,
)

