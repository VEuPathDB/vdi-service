package vdi.lane.imports.config

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import vdi.component.s3.util.S3Config

data class ImportTriggerHandlerConfig(
  val workerPoolSize: UInt,
  val workQueueSize: UInt,
  val kafkaConfig: KafkaConfig,
  val s3Config: S3Config,
  val s3Bucket: BucketName,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    workerPoolSize = env.optUInt(EnvKey.ImportTriggerHandler.WorkerPoolSize)
      ?: Defaults.WorkerPoolSize,
    workQueueSize  = env.optUInt(EnvKey.ImportTriggerHandler.WorkQueueSize)
      ?: Defaults.WorkQueueSize,
    kafkaConfig    = KafkaConfig(env, env.optional(EnvKey.ImportTriggerHandler.KafkaConsumerClientID) ?: "import-handler"),
    s3Config       = S3Config(env),
    s3Bucket       = BucketName(env.require(EnvKey.S3.BucketName)),

    )

  object Defaults {
    const val WorkerPoolSize = 10u
    const val WorkQueueSize = 10u
  }
}
