package vdi.config.raw.kafka

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vdi.config.parse.fields.PartialHostAddress
import vdi.config.parse.serde.HostAddressListDeserializer

data class KafkaConfig(
  @JsonDeserialize(using = HostAddressListDeserializer::class)
  val servers: List<PartialHostAddress>,
  val consumers: KafkaConsumerConfig?,
  val producers: KafkaProducerConfig?,
)
