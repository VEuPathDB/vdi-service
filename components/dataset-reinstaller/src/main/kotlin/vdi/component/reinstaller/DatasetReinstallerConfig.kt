package vdi.component.reinstaller

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.EnvKey
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.require
import vdi.component.s3.util.S3Config

data class DatasetReinstallerConfig(
  val s3Config:     S3Config,
  val s3Bucket:     BucketName,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    s3Config     = S3Config(env),
    s3Bucket     = BucketName(env.require(EnvKey.S3.BucketName)),
  )
}
