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

  object AppDatabases {
    private const val DB_NAME_PREFIX = "DB_CONNECTION_NAME_"
    private const val DB_LDAP_PREFIX = "DB_CONNECTION_LDAP_"
    private const val DB_USER_PREFIX = "DB_CONNECTION_USER_"
    private const val DB_PASS_PREFIX = "DB_CONNECTION_PASS_"
    private const val DB_POOL_PREFIX = "DB_CONNECTION_POOL_SIZE_"

    private val databaseMap = HashMap<String, AppDatabase>(12)

    init { System.getenv().loadDatabases() }

    operator fun contains(key: String) = databaseMap.containsKey(key)
    operator fun get(key: String) = databaseMap[key]
    operator fun iterator() = databaseMap.values.iterator()

    private fun Map<String, String>.loadDatabases() {
      val seen = HashSet<String>(60)

      keys.forEach { key ->
        if (key in seen)
          return@forEach

        when {
          key.startsWith(DB_NAME_PREFIX) -> parse(key.substring(DB_NAME_PREFIX.length), seen)
          key.startsWith(DB_LDAP_PREFIX) -> parse(key.substring(DB_LDAP_PREFIX.length), seen)
          key.startsWith(DB_USER_PREFIX) -> parse(key.substring(DB_USER_PREFIX.length), seen)
          key.startsWith(DB_PASS_PREFIX) -> parse(key.substring(DB_PASS_PREFIX.length), seen)
          key.startsWith(DB_POOL_PREFIX) -> parse(key.substring(DB_POOL_PREFIX.length), seen)
        }
      }
    }

    private fun Map<String, String>.parse(suffix: String, seen: MutableSet<String>) {
      val db = parse(suffix)

      seen.add(DB_NAME_PREFIX + suffix)
      seen.add(DB_LDAP_PREFIX + suffix)
      seen.add(DB_USER_PREFIX + suffix)
      seen.add(DB_PASS_PREFIX + suffix)
      seen.add(DB_POOL_PREFIX + suffix)

      databaseMap[db.name] = db
    }

    private fun Map<String, String>.parse(suffix: String) =
      AppDatabase(
        name = get(DB_NAME_PREFIX + suffix) ?: parsingFailed(suffix),
        ldap = get(DB_LDAP_PREFIX + suffix) ?: parsingFailed(suffix),
        username = get(DB_USER_PREFIX + suffix) ?: parsingFailed(suffix),
        password = get(DB_PASS_PREFIX + suffix) ?: parsingFailed(suffix),
        poolSize = get(DB_POOL_PREFIX + suffix)?.toInt() ?: 5
      )
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