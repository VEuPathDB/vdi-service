package vdi.lib.pruner

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import vdi.core.config.loadAndCacheStackConfig
import vdi.config.raw.vdi.ObjectStoreConfig
import vdi.config.raw.vdi.daemons.PrunerConfig
import vdi.lib.s3.util.S3Config

data class PrunerConfig(
  val s3Config: S3Config,
  val bucketName: BucketName,
  val pruneAge:   Duration,
) {
  constructor(): this(
    loadAndCacheStackConfig().vdi.objectStore,
    loadAndCacheStackConfig().vdi.daemons?.pruner,
  )

  constructor(oConfig: ObjectStoreConfig, pConfig: PrunerConfig?): this (
    s3Config   = S3Config(oConfig),
    bucketName = BucketName(oConfig.bucketName),
    pruneAge   = pConfig?.retentionWindow ?: DeletionThreshold
  )

  companion object {
    inline val DeletionThreshold
      get() = 30.days
  }
}
