package vdi.daemon.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MinIOEventRecordSource(
  @param:JsonProperty("host")
  @field:JsonProperty("host")
  val host: String,

  @param:JsonProperty("port")
  @field:JsonProperty("port")
  val port: String,

  @param:JsonProperty("userAgent")
  @field:JsonProperty("userAgent")
  val userAgent: String,
)