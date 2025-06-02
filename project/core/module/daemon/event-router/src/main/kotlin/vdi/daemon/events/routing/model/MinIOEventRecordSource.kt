package vdi.daemon.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MinIOEventRecordSource(
  @JsonProperty("host")
  val host: String,

  @JsonProperty("port")
  val port: String,

  @JsonProperty("userAgent")
  val userAgent: String,
)