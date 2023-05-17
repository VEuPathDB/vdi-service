package org.veupathdb.vdi.lib.s3.datasets.util

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.vdi.lib.common.env.*

/**
 * Constructs a new [S3Config] instance from the given environment map using the
 * environment variable names defined in [EnvKey.S3].
 *
 * @param env Environment map.
 *
 * @return Constructed [S3Config] instance.
 */
fun S3Config(env: Environment): S3Config =
  S3Config(
    url       = env.require(EnvKey.S3.Host),
    port      = env.reqUShort(EnvKey.S3.Port),
    secure    = env.reqBool(EnvKey.S3.UseHTTPS),
    accessKey = env.require(EnvKey.S3.AccessToken),
    secretKey = env.require(EnvKey.S3.SecretKey),
  )