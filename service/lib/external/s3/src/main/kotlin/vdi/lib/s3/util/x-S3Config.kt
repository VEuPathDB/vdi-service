package vdi.lib.s3.util

import org.veupathdb.lib.s3.s34k.S3Config
import vdi.lib.config.vdi.ObjectStoreConfig
import vdi.lib.env.EnvKey

/**
 * Constructs a new [S3Config] instance from the given environment map using the
 * environment variable names defined in [EnvKey.S3].
 *
 * @param conf Object store configuration parsed from YAML stack config.
 *
 * @return Constructed [S3Config] instance.
 */
fun S3Config(conf: ObjectStoreConfig): S3Config =
  S3Config(
    url       = conf.host.host,
    port      = conf.host.port ?: 9000u,
    secure    = conf.https ?: true,
    accessKey = conf.accessToken.value,
    secretKey = conf.secretKey.value,
  )
