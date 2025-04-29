package vdi.lib.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty

sealed class LaneConfigBase(
  @JsonProperty("kafkaConsumerId")
  val kafkaConsumerID: String?,
  val memoryQueueSize: UByte?,
  val workerCount: UByte?,
  val eventChannel: String?,
  val eventKey: String?,
)
