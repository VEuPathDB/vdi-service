package vdi.daemon.reconciler

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import vdi.component.env.EnvKey
import vdi.component.kafka.router.KafkaRouterConfig
import vdi.component.s3.util.S3Config
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

data class ReconcilerConfig(
  val kafkaRouterConfig: KafkaRouterConfig,
  val s3Config:          S3Config,
  val s3Bucket:          BucketName,
  val reconcilerEnabled: Boolean,
  val fullRunInterval:   Duration,
  val slimRunInterval:   Duration,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    kafkaRouterConfig = KafkaRouterConfig(env, "reconciler"),
    s3Config          = S3Config(env),
    s3Bucket          = BucketName(env.require(EnvKey.S3.BucketName)),
    reconcilerEnabled = env.optBool(EnvKey.Reconciler.Enabled) ?: DefaultEnabledValue,
    fullRunInterval   = env.optDuration(EnvKey.Reconciler.FullRunInterval) ?: DefaultFullRunInterval,
    slimRunInterval   = env.optDuration(EnvKey.Reconciler.SlimRunInterval) ?: DefaultSlimRunInterval,
  )

  companion object {
    inline val DefaultEnabledValue get() = true
    inline val DefaultFullRunInterval get() = 6.hours
    inline val DefaultSlimRunInterval get() = 5.minutes
  }
}

