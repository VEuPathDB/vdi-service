package vdi.lib.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty

class ConsumerLaneConfig(
  @JsonProperty("kafkaConsumerId")
  kafkaConsumerID: String?,
  memoryQueueSize: UByte?,
  workerCount: UByte?,
  eventChannel: String?,
  eventKey: String?,
): LaneConfigBase(kafkaConsumerID, memoryQueueSize, workerCount, eventChannel, eventKey)
