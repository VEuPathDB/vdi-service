package vdi.config.raw.vdi.lanes

import com.fasterxml.jackson.annotation.JsonProperty

sealed class LaneConfigBase(
  @param:JsonProperty("kafkaConsumerId")
  @field:JsonProperty("kafkaConsumerId")
  val kafkaConsumerID: String?,
  val inMemoryQueueSize: UByte?,
  val workerCount: UByte?,
  val eventChannel: String?,
  val eventKey: String?,
)
