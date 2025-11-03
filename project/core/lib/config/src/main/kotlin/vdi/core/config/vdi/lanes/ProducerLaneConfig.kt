package vdi.core.config.vdi.lanes

import com.fasterxml.jackson.annotation.JsonProperty

class ProducerLaneConfig(
  @JsonProperty("kafkaConsumerId")
  kafkaConsumerID: String?,
  memoryQueueSize: UByte?,
  workerCount: UByte?,
  eventChannel: String?,
  eventKey: String?,
  @param:JsonProperty("kafkaProducerId")
  @field:JsonProperty("kafkaProducerId")
  val kafkaProducerID: String?
): LaneConfigBase(kafkaConsumerID, memoryQueueSize, workerCount, eventChannel, eventKey)
