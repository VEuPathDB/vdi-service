package vdi.lib.reconciler

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import vdi.core.config.loadAndCacheStackConfig
import vdi.config.raw.vdi.VDIConfig
import vdi.lib.kafka.router.KafkaRouterConfig
import vdi.lib.s3.util.S3Config

internal data class ReconcilerConfig(
  val kafkaRouterConfig: KafkaRouterConfig,
  val s3Config:          S3Config,
  val s3Bucket:          BucketName,
  val deletesEnabled:    Boolean,
) {
  constructor(): this(loadAndCacheStackConfig().vdi)

  constructor(config: VDIConfig): this(
    kafkaRouterConfig = KafkaRouterConfig("reconciler", config.kafka, config.lanes),
    s3Config          = S3Config(config.objectStore),
    s3Bucket          = BucketName(config.objectStore.bucketName),
    deletesEnabled    = config.daemons?.reconciler?.performDeletes ?: DefaultDeletesEnabled,
  )

  companion object {
    const val DefaultDeletesEnabled = true
  }
}

