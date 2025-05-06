package vdi.lib.config.vdi.lanes

import com.fasterxml.jackson.annotation.JsonProperty

sealed class LaneConfigBase(
  @JsonProperty("kafkaConsumerId")
  val kafkaConsumerID: String?,
  val inMemoryQueueSize: UByte?,
  val workerCount: UByte?,
  val eventChannel: String?,
  val eventKey: String?,
)
