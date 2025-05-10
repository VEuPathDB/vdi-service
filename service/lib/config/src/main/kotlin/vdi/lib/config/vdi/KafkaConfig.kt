package vdi.lib.config.vdi

import vdi.lib.config.common.HostAddress

data class KafkaConfig(
  val servers: List<HostAddress>,
  val consumers: KafkaConsumerConfig?,
  val producers: KafkaProducerConfig?,
)
