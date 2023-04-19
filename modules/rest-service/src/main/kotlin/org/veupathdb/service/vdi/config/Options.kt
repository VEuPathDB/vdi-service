package org.veupathdb.service.vdi.config

import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.vdi.lib.common.env.EnvKey

object Options : Options() {

  object S3 {
    val host        = requireEnv(EnvKey.S3.Host)
    val port        = requireEnv(EnvKey.S3.Port).toUShort()
    val useHttps    = requireEnv(EnvKey.S3.UseHTTPS).toBoolean()
    val accessToken = requireEnv(EnvKey.S3.AccessToken)
    val secretKey   = requireEnv(EnvKey.S3.SecretKey)
    val bucketName  = requireEnv(EnvKey.S3.BucketName)
  }
}

private fun requireEnv(key: String): String =
  System.getenv(key)
    .also { if (it.isNullOrBlank()) throw IllegalStateException("missing required environment variable $key") }
