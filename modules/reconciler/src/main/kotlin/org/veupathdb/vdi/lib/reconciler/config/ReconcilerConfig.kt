package org.veupathdb.vdi.lib.reconciler.config

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.EnvKey
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.require
import org.veupathdb.vdi.lib.kafka.EventSource
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfig
import org.veupathdb.vdi.lib.s3.datasets.util.S3Config
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ReconcilerConfig(
  val kafkaRouterConfig:            KafkaRouterConfig,
  val s3Config:                     S3Config,
  val s3Bucket:                     BucketName,
  val runInterval: Duration,
  val reconcilerEnabled:            Boolean
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    kafkaRouterConfig = KafkaRouterConfig(env, "reconciler", EventSource.Reconciler),

    s3Config = S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),

    runInterval = env.getOrDefault(EnvKey.Reconciler.RunIntervalSeconds, "21600").toLong().seconds,

    reconcilerEnabled = env.getOrDefault(EnvKey.Reconciler.ReconcilerEnabled, "true").toBoolean()
  )
}

