package vdi.lib.reconciler

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.optBool
import org.veupathdb.vdi.lib.common.env.require
import vdi.lib.env.EnvKey
import vdi.lib.kafka.router.KafkaRouterConfig
import vdi.lib.s3.util.S3Config

internal data class ReconcilerConfig(
  val kafkaRouterConfig: KafkaRouterConfig,
  val s3Config:          S3Config,
  val s3Bucket:          BucketName,
  val deletesEnabled:    Boolean,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    kafkaRouterConfig = KafkaRouterConfig(env, "reconciler"),
    s3Config          = vdi.lib.s3.util.S3Config(env),
    s3Bucket          = BucketName(env.require(EnvKey.S3.BucketName)),
    deletesEnabled    = env.optBool(EnvKey.Reconciler.DeletesEnabled) ?: DefaultDeletesEnabled,
  )

  companion object {
    const val DefaultDeletesEnabled = true
  }
}

