package vdi.lib.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty

data class EventRouterConfig(
  @JsonProperty("kafkaProducerId")
  val kafkaProducerID: String?
)
