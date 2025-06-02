package vdi.config.raw.vdi.daemons

import com.fasterxml.jackson.annotation.JsonProperty

data class EventRouterConfig(
  @param:JsonProperty("kafkaProducerId")
  @field:JsonProperty("kafkaProducerId")
  val kafkaProducerID: String?
)
