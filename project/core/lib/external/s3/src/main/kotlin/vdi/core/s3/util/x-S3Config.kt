package vdi.core.s3.util

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import vdi.core.config.vdi.ObjectStoreConfig

/**
 * @param conf Object store configuration parsed from YAML stack config.
 *
 * @return Constructed [S3Config] instance.
 */
fun S3Config(conf: ObjectStoreConfig): S3Config =
  S3Config(
    url       = conf.server.host,
    port      = conf.server.port ?: 9000u,
    secure    = conf.https ?: true,
    accessKey = conf.accessToken.asString,
    secretKey = conf.secretKey.asString,
  )

inline val ObjectStoreConfig.bucket get() = BucketName(bucketName)
