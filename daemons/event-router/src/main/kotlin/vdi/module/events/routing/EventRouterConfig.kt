package vdi.module.events.routing

import org.veupathdb.vdi.lib.common.env.EnvKey
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.require
import org.veupathdb.vdi.lib.kafka.EventSource
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfig
import org.veupathdb.vdi.lib.rabbit.RabbitMQConfig

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

