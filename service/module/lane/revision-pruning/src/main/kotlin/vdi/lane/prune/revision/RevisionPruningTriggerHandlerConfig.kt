package vdi.lane.prune.revision

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.optUInt
import org.veupathdb.vdi.lib.common.env.optional
import org.veupathdb.vdi.lib.common.env.require
import vdi.lib.env.EnvKey
import vdi.lib.env.Environment
import vdi.lib.kafka.KafkaConsumerConfig
import vdi.lib.kafka.router.KafkaRouterConfigDefaults
import vdi.lib.s3.util.S3Config

data class RevisionPruningTriggerHandlerConfig(
  val jobQueueSize: UInt,
  val workerPoolSize: UInt,
  val s3Config: S3Config,
  val s3Bucket: BucketName,
  val kafkaConsumerConfig: KafkaConsumerConfig,
  val revisionPruningTopic: String,
  val revisionPruningMessageKey: String,
) {
  constructor(): this(System.getenv())

  constructor(env: Environment): this(
    jobQueueSize = env.optUInt(EnvKey.RevisionPruningTriggerHandler.WorkQueueSize)
      ?: Defaults.JobQueueSize,

    workerPoolSize = env.optUInt(EnvKey.RevisionPruningTriggerHandler.WorkerPoolSize)
      ?: Defaults.WorkerPoolSize,

    s3Config = S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),

    kafkaConsumerConfig = KafkaConsumerConfig(
      env.optional(
        EnvKey.RevisionPruningTriggerHandler.KafkaConsumerClientID
      ) ?: Defaults.KafkaConsumerClientID,
      env
    ),

    revisionPruningTopic = env.optional(EnvKey.Kafka.Topic.RevisionPruningTriggers)
      ?: Defaults.KafkaTopic,

    revisionPruningMessageKey = env.optional(EnvKey.Kafka.MessageKey.RevisionPruningTriggers)
      ?: Defaults.KafkaMessageKey
  )

  object Defaults {
    const val JobQueueSize = 10u
    const val WorkerPoolSize = 10u
    const val KafkaConsumerClientID = "revision-pruner-receive"

    inline val KafkaTopic
      get() = KafkaRouterConfigDefaults.REVISION_PRUNE_TRIGGER_TOPIC

    inline val KafkaMessageKey
      get() = KafkaRouterConfigDefaults.REVISION_PRUNE_TRIGGER_MESSAGE_KEY
  }
}

