package vdi.lib.config.vdi

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.veupathdb.vdi.lib.config.PartialHostAddress
import org.veupathdb.vdi.lib.config.serde.HostAddressListDeserializer

data class KafkaConfig(
  @JsonDeserialize(using = HostAddressListDeserializer::class)
  val servers: List<PartialHostAddress>,
  val consumers: KafkaConsumerConfig?,
  val producers: KafkaProducerConfig?,
)
