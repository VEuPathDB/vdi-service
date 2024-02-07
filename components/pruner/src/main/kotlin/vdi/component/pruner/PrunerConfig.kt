package vdi.component.pruner

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.EnvKey
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.optDuration
import org.veupathdb.vdi.lib.common.env.require
import org.veupathdb.vdi.lib.s3.datasets.util.S3Config
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class PrunerConfig(
  val s3Config: S3Config,
  val bucketName: BucketName,
  val pruneAge:   Duration,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this (
    s3Config   = S3Config(env),
    bucketName = BucketName(env.require(EnvKey.S3.BucketName)),
//    pruneAge   = env.optDuration(EnvKey.Pruner.DeletionThreshold)
//      ?: PrunerConfigDefaults.DeletionThreshold
    // FIXME: REMOVE THIS AFTER MINIO WIPE
    pruneAge = 5.minutes
  )
}
