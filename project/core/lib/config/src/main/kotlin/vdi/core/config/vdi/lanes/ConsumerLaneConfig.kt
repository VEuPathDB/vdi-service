package vdi.core.config.vdi.lanes

import com.fasterxml.jackson.annotation.JsonProperty

class ConsumerLaneConfig(
  @JsonProperty("kafkaConsumerId")
  kafkaConsumerID: String?,
  memoryQueueSize: UByte?,
  workerCount: UByte?,
  eventChannel: String?,
  eventKey: String?,
): LaneConfigBase(kafkaConsumerID, memoryQueueSize, workerCount, eventChannel, eventKey)
