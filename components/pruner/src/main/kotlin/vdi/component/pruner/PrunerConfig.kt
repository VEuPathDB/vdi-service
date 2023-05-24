package vdi.component.pruner

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.s3.datasets.util.S3Config
import kotlin.time.Duration

data class PrunerConfig(
  val s3Config: S3Config,
  val bucketName: BucketName,
  val pruneAge:   Duration,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this (
    s3Config   = S3Config(env),
    bucketName = BucketName(env.require(EnvKey.S3.BucketName)),
    pruneAge   = env.optDuration(EnvKey.Pruner.DeletionThreshold)
      ?: PrunerConfigDefaults.DeletionThreshold
  )
}
