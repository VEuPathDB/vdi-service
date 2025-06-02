package vdi.core.install.retry

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import vdi.config.loadAndCacheStackConfig
import vdi.config.raw.vdi.ObjectStoreConfig
import vdi.core.s3.util.S3Config

data class DatasetReinstallerConfig(
  val s3Config:     S3Config,
  val s3Bucket:     BucketName,
) {
  constructor() : this(loadAndCacheStackConfig().vdi.objectStore)

  constructor(conf: ObjectStoreConfig) : this(
    s3Config     = S3Config(conf),
    s3Bucket     = BucketName(conf.bucketName),
  )
}
