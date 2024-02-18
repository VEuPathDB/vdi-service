package org.veupathdb.vdi.lib.db.app

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import oracle.jdbc.OracleDriver
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.ldap.LDAP

object AppDatabaseRegistry {

  private val log = LoggerFactory.getLogger(javaClass)

  private val dataSources = HashMap<String, AppDBRegistryEntry>(12)

  init {
    init(System.getenv())
  }

  operator fun contains(key: String): Boolean = dataSources.containsKey(key)

  operator fun get(key: String): AppDBRegistryEntry? = dataSources[key]

  operator fun iterator() =
    dataSources.entries
      .asSequence()
      .map { (key, value) -> key to value }
      .iterator()

  fun require(key: String): AppDBRegistryEntry =
    get(key) ?: throw IllegalStateException("required AppDB connection $key was not registered with AppDatabases")

  internal fun init(env: Environment) {
    dataSources.clear()

    val seen = HashSet<String>(32)

    env.keys.asSequence()
      .filter { hasEnvPrefix(it) }
      .map { getEnvName(it) }
      .filterNotNull()
      .filter { it !in seen }
      .forEach {
        parseEnvironmentChunk(env, it)
        seen.add(it)
      }
  }

  private fun hasEnvPrefix(key: String) =
    key.startsWith(EnvKey.AppDB.DBNamePrefix) ||
    key.startsWith(EnvKey.AppDB.DBLDAPPrefix) ||
    key.startsWith(EnvKey.AppDB.DBPassPrefix) ||
    key.startsWith(EnvKey.AppDB.DBPoolPrefix) ||
    key.startsWith(EnvKey.AppDB.DBDataSchemaPrefix) ||
    key.startsWith(EnvKey.AppDB.DBControlSchemaPrefix) ||
    key.startsWith(EnvKey.AppDB.DBEnabledPrefix)

  private fun getEnvName(key: String) =
    when {
      key.startsWith(EnvKey.AppDB.DBNamePrefix)          -> getEnvName(EnvKey.AppDB.DBNamePrefix, key)
      key.startsWith(EnvKey.AppDB.DBLDAPPrefix)          -> getEnvName(EnvKey.AppDB.DBLDAPPrefix, key)
      key.startsWith(EnvKey.AppDB.DBPassPrefix)          -> getEnvName(EnvKey.AppDB.DBPassPrefix, key)
      key.startsWith(EnvKey.AppDB.DBPoolPrefix)          -> getEnvName(EnvKey.AppDB.DBPoolPrefix, key)
      key.startsWith(EnvKey.AppDB.DBDataSchemaPrefix)    -> getEnvName(EnvKey.AppDB.DBDataSchemaPrefix, key)
      key.startsWith(EnvKey.AppDB.DBControlSchemaPrefix) -> getEnvName(EnvKey.AppDB.DBControlSchemaPrefix, key)
      key.startsWith(EnvKey.AppDB.DBEnabledPrefix)       -> getEnvName(EnvKey.AppDB.DBEnabledPrefix, key)
      else                                               -> null
    }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun getEnvName(prefix: String, key: String) =
    key.substring(prefix.length)

  private fun parseEnvironmentChunk(env: Environment, key: String) {
    val enabled = env.reqBool(EnvKey.AppDB.DBEnabledPrefix + key)

    if (!enabled) {
      log.info("Database {} is marked as disabled, skipping.", key)
      return
    }

    val name = env.require(EnvKey.AppDB.DBNamePrefix + key)
    val ldap = env.require(EnvKey.AppDB.DBLDAPPrefix + key)
    val pass = env.require(EnvKey.AppDB.DBPassPrefix + key)
    val pool = env.reqUByte(EnvKey.AppDB.DBPoolPrefix + key)
    val ctls = env.require(EnvKey.AppDB.DBControlSchemaPrefix + key)
    val data = env.require(EnvKey.AppDB.DBDataSchemaPrefix + key)

    log.info(
      """registering database {} with the following details:
      Name: {}
      TNS: {}
      Pool Size: {}
      User/Schema: {}""",
      name,
      name,
      ldap,
      pool,
      ctls
    )

    log.debug("looking up LDAP record for database {}", name)

    val desc = LDAP.requireSingularOracleNetDesc(ldap)

    log.info("constructing a DataSource for database {}", name)

    val ds = try {
      HikariConfig()
        .apply {
          jdbcUrl = makeJDBCOracleConnectionString(desc.host, desc.port, desc.serviceName)
          username = ctls
          password = pass
          maximumPoolSize = pool.toInt()
          driverClassName = OracleDriver::class.java.name
        }
        .let(::HikariDataSource)
    } catch (e: Throwable) {
      throw IllegalStateException("error encountered while attempting to create a JDBC connection to ${desc.serviceName}", e)
    }

    dataSources[name] = AppDBRegistryEntry(name, desc.host, desc.port, ds, data, ctls)
  }

  private fun makeJDBCOracleConnectionString(host: String, port: UShort, name: String) =
    "jdbc:oracle:thin:@//$host:$port/$name"
}