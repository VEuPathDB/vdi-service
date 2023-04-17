package org.veupathdb.service.vdi.config

import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.ldap.LDAPHost
import org.veupathdb.vdi.lib.common.env.EnvKey

object Options : Options() {

  object LDAP {
    val ldapServers  = requireEnv("LDAP_SERVER").split(',').map(LDAPHost.Companion::ofString)
    val oracleBaseDN = requireEnv("ORACLE_BASE_DN")
  }

  object S3 {
    val host        = requireEnv(EnvKey.S3.Host)
    val port        = requireEnv(EnvKey.S3.Port).toUShort()
    val useHttps    = requireEnv(EnvKey.S3.UseHTTPS).toBoolean()
    val accessToken = requireEnv(EnvKey.S3.AccessToken)
    val secretKey   = requireEnv(EnvKey.S3.SecretKey)
    val bucketName  = requireEnv(EnvKey.S3.BucketName)
  }

  object CacheDB {
    val host     = requireEnv(EnvKey.CacheDB.Host)
    val port     = requireEnv(EnvKey.CacheDB.Port).toUShort()
    val name     = requireEnv(EnvKey.CacheDB.Name)
    val username = requireEnv(EnvKey.CacheDB.Username)
    val password = requireEnv(EnvKey.CacheDB.Password)
    val poolSize = optionalEnv(EnvKey.CacheDB.PoolSize)?.toUByte() ?: 20u
  }
}

private fun optionalEnv(key: String): String? =
  System.getenv(key)
    .let { if (it.isNullOrBlank()) null else it }

private fun requireEnv(key: String): String =
  System.getenv(key)
    .also { if (it.isNullOrBlank()) throw IllegalStateException("missing required environment variable $key") }

private fun parsingFailed(suffix: String): Nothing =
  throw IllegalStateException("one or more database connection environment variables was not set with the suffix: $suffix")