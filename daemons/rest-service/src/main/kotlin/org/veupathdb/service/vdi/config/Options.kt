package org.veupathdb.service.vdi.config

import org.veupathdb.lib.container.jaxrs.config.Options
import vdi.component.env.EnvKey
import java.math.BigInteger

object Options : Options() {

  object S3 {
    val host        = requireEnv(EnvKey.S3.Host)
    val port        = requireEnv(EnvKey.S3.Port).toUShort()
    val useHttps    = requireEnv(EnvKey.S3.UseHTTPS).toBoolean()
    val accessToken = requireEnv(EnvKey.S3.AccessToken)
    val secretKey   = requireEnv(EnvKey.S3.SecretKey)
    val bucketName  = requireEnv(EnvKey.S3.BucketName)
  }

  object Quota {
    val maxUploadSize = uLongOr(EnvKey.Quotas.MaxUploadFileSize, 1073741824uL)
    val quotaLimit    = uLongOr(EnvKey.Quotas.UserUploadQuota, 10737418240uL)
  }
}

private fun requireEnv(key: String): String =
  System.getenv(key)
    .also { if (it.isNullOrBlank()) throw IllegalStateException("missing required environment variable $key") }

private fun uLongOr(key: String, other: ULong): ULong {
  val value = System.getenv(key)?.takeIf { it.isNotBlank() }
    ?.toBigInteger()
    ?: return other

  if (value < BigInteger.ZERO || value > BigInteger(ULong.MAX_VALUE.toString()))
    throw IllegalStateException("value $key must be an unsigned uint64 value")

  return value.toString().toULong()
}