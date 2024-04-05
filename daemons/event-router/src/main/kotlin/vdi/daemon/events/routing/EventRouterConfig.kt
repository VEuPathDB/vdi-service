package vdi.daemon.events.routing

import org.veupathdb.vdi.lib.common.env.require
import vdi.component.env.EnvKey
import vdi.component.env.Environment
import vdi.component.kafka.EventSource
import vdi.component.kafka.router.KafkaRouterConfig
import vdi.component.rabbit.RabbitMQConfig

data class EventRouterConfig(
  val rabbitConfig: RabbitMQConfig,
  val s3Bucket: String,
  val kafkaConfig: KafkaRouterConfig
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    rabbitConfig = RabbitMQConfig(env),
    s3Bucket     = env.require(EnvKey.S3.BucketName),
    kafkaConfig  = KafkaRouterConfig(env, "event-router", EventSource.ObjectStore)
  )
}

