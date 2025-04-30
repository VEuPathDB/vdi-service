package vdi.lib.config.vdi.lanes

import com.fasterxml.jackson.annotation.JsonProperty

class ProducerLaneConfig(
  @JsonProperty("kafkaConsumerId")
  kafkaConsumerID: String?,
  memoryQueueSize: UByte?,
  workerCount: UByte?,
  eventChannel: String?,
  eventKey: String?,
  @JsonProperty("kafkaProducerId")
  val kafkaProducerID: String?
): LaneConfigBase(kafkaConsumerID, memoryQueueSize, workerCount, eventChannel, eventKey)
