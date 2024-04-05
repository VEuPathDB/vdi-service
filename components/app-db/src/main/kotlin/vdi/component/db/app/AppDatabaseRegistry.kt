package vdi.component.db.app

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import oracle.jdbc.OracleDriver
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.env.*
import vdi.component.ldap.LDAP

@Suppress("NOTHING_TO_INLINE")
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

  fun size() = dataSources.size

  fun require(key: String): AppDBRegistryEntry =
    get(key) ?: throw IllegalStateException("required AppDB connection $key was not registered with AppDatabases")

  internal fun init(env: Environment) {
    dataSources.clear()

    DBEnvGroup.fromEnvironment(env)
      .onEach { requireFullChunk(it) }
      .forEach { parseChunk(it) }
  }

  private inline fun throwForMissingVar(envVar: String): Nothing =
    throw IllegalStateException("one or more app database definition blocks in the service environment is missing its '$envVar' value")

  private fun requireFullChunk(db: DBEnvGroup) {
    db.name          ?: throwForMissingVar(EnvKey.AppDB.DBNamePrefix)
    db.ldap          ?: throwForMissingVar(EnvKey.AppDB.DBLDAPPrefix)
    db.pass          ?: throwForMissingVar(EnvKey.AppDB.DBPassPrefix)
    db.controlSchema ?: throwForMissingVar(EnvKey.AppDB.DBControlSchemaPrefix)
    db.dataSchema    ?: throwForMissingVar(EnvKey.AppDB.DBDataSchemaPrefix)
  }

  private fun parseChunk(env: DBEnvGroup) {
    if (env.enabled != true) {
      log.info("Database {} is marked as disabled, skipping.", env.name)
      return
    }

    log.info(
      """registering database {} with the following details:
      Name: {}
      TNS: {}
      Pool Size: {}
      User/Schema: {}""",
      env.name,
      env.name,
      env.ldap,
      env.poolSize,
      env.controlSchema
    )

    log.debug("looking up LDAP record for database {}", env.name)

    val desc = LDAP.requireSingularOracleNetDesc(env.ldap!!)

    log.info("constructing a DataSource for database {}", env.name)

    val ds = try {
      HikariConfig()
        .apply {
          jdbcUrl = makeJDBCOracleConnectionString(desc.host, desc.port, desc.serviceName)
          username = env.controlSchema
          password = env.pass!!.unwrap()
          maximumPoolSize = env.poolSize!!.toInt()
          driverClassName = OracleDriver::class.java.name
        }
        .let(::HikariDataSource)
    } catch (e: Throwable) {
      throw IllegalStateException("error encountered while attempting to create a JDBC connection to ${desc.serviceName}", e)
    }

    dataSources[env.name!!] = AppDBRegistryEntry(env.name!!, desc.host, desc.port, ds, env.dataSchema!!, env.controlSchema!!)
  }

  private fun makeJDBCOracleConnectionString(host: String, port: UShort, name: String) =
    "jdbc:oracle:thin:@//$host:$port/$name"
}