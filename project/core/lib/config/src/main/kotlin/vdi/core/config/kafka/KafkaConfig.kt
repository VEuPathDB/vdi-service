package vdi.core.config.kafka

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vdi.config.parse.fields.PartialHostAddress
import vdi.config.parse.serde.HostAddressListDeserializer

data class KafkaConfig(
  @param:JsonDeserialize(using = HostAddressListDeserializer::class)
  val servers: List<PartialHostAddress>,
  val consumers: KafkaConsumerConfig?,
  val producers: KafkaProducerConfig?,
)
