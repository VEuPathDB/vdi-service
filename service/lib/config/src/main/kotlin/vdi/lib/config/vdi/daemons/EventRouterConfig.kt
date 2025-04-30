package vdi.lib.config.vdi.daemons

import com.fasterxml.jackson.annotation.JsonProperty

data class EventRouterConfig(
  @JsonProperty("kafkaProducerId")
  val kafkaProducerID: String?
)
