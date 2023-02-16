package org.veupathdb.service.vdi.config

import org.veupathdb.lib.container.jaxrs.config.Options

object Options : Options() {

  object Server {
    val port = requireEnv("VDI_SERVICE_HTTP_PORT").toUShort()
  }

  object LDAP {
    val ldapServers = requireEnv("LDAP_SERVERS").split(',')
  }

  object Auth {
    val authSecretKey = requireEnv("AUTH_SECRET_KEY")
  }

  object GlobalRabbit {
    val username = requireEnv("GLOBAL_RABBIT_USERNAME")
    val password = requireEnv("GLOBAL_RABBIT_PASSWORD")
    val host = requireEnv("GLOBAL_RABBIT_HOST")
    val port = requireEnv("GLOBAL_RABBIT_PORT").toUShort()
  }

  object InternalRabbit {
    val username = requireEnv("INTERNAL_RABBIT_USERNAME")
    val password = requireEnv("INTERNAL_RABBIT_PASSWORD")
    val host = requireEnv("INTERNAL_RABBIT_HOST")
    val port = requireEnv("INTERNAL_RABBIT_PORT").toUShort()
  }

  object S3 {
    val host = requireEnv("S3_HOST")
    val port = requireEnv("S3_PORT")
    val useHttps = requireEnv("S3_USE_HTTPS").toBoolean()
    val accessToken = requireEnv("S3_ACCESS_TOKEN")
    val secretKey = requireEnv("S3_SECRET_KEY")
    val uploadBucketName = requireEnv("S3_UPLOAD_BUCKET_NAME")
    val datasetBucketName = requireEnv("S3_DATASET_BUCKET_NAME")
  }

  object QueueDB {
    val host = requireEnv("QUEUE_DB_HOST")
    val port = requireEnv("QUEUE_DB_PORT").toUShort()
    val name = requireEnv("QUEUE_DB_NAME")
    val username = requireEnv("QUEUE_DB_USERNAME")
    val password = requireEnv("QUEUE_DB_PASSWORD")
    val poolSize = requireEnv("QUEUE_DB_POOL_SIZE").toInt()
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
        poolSize = get(DB_POOL_PREFIX + suffix)?.toInt() ?: parsingFailed(suffix)
      )
  }
}

private fun requireEnv(key: String): String =
  System.getenv(key)
    .also { if (it.isNullOrBlank()) throw IllegalStateException("missing required environment variable $key") }

private fun parsingFailed(suffix: String): Nothing =
  throw IllegalStateException("one or more database connection environment variables was not set with the suffix: $suffix")