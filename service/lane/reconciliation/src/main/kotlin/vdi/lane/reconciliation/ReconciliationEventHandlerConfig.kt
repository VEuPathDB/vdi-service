package vdi.lane.reconciliation

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.optUInt
import org.veupathdb.vdi.lib.common.env.optional
import org.veupathdb.vdi.lib.common.env.require
import vdi.component.env.EnvKey
import vdi.component.env.Environment
import vdi.component.kafka.KafkaConsumerConfig
import vdi.component.kafka.router.KafkaRouterConfig
import vdi.component.kafka.router.KafkaRouterConfigDefaults
import vdi.component.s3.util.S3Config

data class ReconciliationEventHandlerConfig(
  val jobQueueSize: UInt,
  val workerPoolSize: UInt,
  val kafkaConsumerConfig: KafkaConsumerConfig,
  val kafkaRouterConfig: KafkaRouterConfig,
  val kafkaTopic: String,
  val kafkaMessageKey: String,
  val s3Config: S3Config,
  val s3Bucket: BucketName,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    jobQueueSize = env.optUInt(EnvKey.ReconciliationTriggerHandler.WorkQueueSize)
      ?: Defaults.JobQueueSize,

    workerPoolSize = env.optUInt(EnvKey.ReconciliationTriggerHandler.WorkerPoolSize)
      ?: Defaults.WorkerPoolSize,

    kafkaConsumerConfig = KafkaConsumerConfig(env.optional(EnvKey.ReconciliationTriggerHandler.KafkaConsumerClientID) ?: "reconciliation-handler", env),

    kafkaRouterConfig = KafkaRouterConfig(env, "reconciliation-event-handler"),

    kafkaTopic = env.optional(EnvKey.Kafka.Topic.ReconciliationTriggers)
      ?: Defaults.ReconciliationTopic,

    kafkaMessageKey = env.optional(EnvKey.Kafka.MessageKey.ReconciliationTriggers)
      ?: Defaults.ReconciliationMessageKey,

    s3Config = S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName))
  )

  object Defaults {
    const val JobQueueSize   = 10u
    const val WorkerPoolSize = 10u

    inline val ReconciliationTopic
      get() = KafkaRouterConfigDefaults.RECONCILIATION_TRIGGER_TOPIC

    inline val ReconciliationMessageKey
      get() = KafkaRouterConfigDefaults.RECONCILIATION_TRIGGER_MESSAGE_KEY
  }
}

