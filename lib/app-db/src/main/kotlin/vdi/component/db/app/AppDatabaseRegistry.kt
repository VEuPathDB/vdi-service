package vdi.component.db.app

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import oracle.jdbc.OracleDriver
import org.slf4j.LoggerFactory
import org.veupathdb.lib.ldap.OracleNetDesc
import org.veupathdb.vdi.lib.common.env.DBEnvGroup
import org.veupathdb.vdi.lib.common.env.EnvKey
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.field.SecretString
import vdi.component.ldap.LDAP
import javax.sql.DataSource

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
    db.pass          ?: throwForMissingVar(EnvKey.AppDB.DBPassPrefix)
    db.controlSchema ?: throwForMissingVar(EnvKey.AppDB.DBControlSchemaPrefix)
    db.dataSchema    ?: throwForMissingVar(EnvKey.AppDB.DBDataSchemaPrefix)

    if (db.platform === "postgresql") {
      db.pgDbName    ?: throwForMissingVar(EnvKey.AppDB.DBPGNamePrefix)
      db.host        ?: throwForMissingVar(EnvKey.AppDB.DBHostPrefix)
      db.port        ?: throwForMissingVar(EnvKey.AppDB.DBPortPrefix)
    } else {
      db.ldap        ?: throwForMissingVar(EnvKey.AppDB.DBLDAPPrefix)
    }
  }

  private fun parseChunk(env: DBEnvGroup) {
    if (env.enabled != true) {
      log.info("Database {} is marked as disabled, skipping.", env.name)
      return
    }

    val defaultPostgresPort: UShort = 5432u
    val poolSize = (env.poolSize ?: DefaultPoolSize).toInt()
    val postgresPort: UShort = (env.port ?: defaultPostgresPort)

    log.info(
      """registering database {} with the following details:
      Name: {}
      TNS: {}
      Pool Size: {}
      Port: {}
      DBNamme: {}
      User/Schema: {}""",
      env.name,
      env.name,
      env.ldap,
      poolSize,
      env.port,
      env.pgDbName,
      env.controlSchema
    )

    log.info("constructing a DataSource for database {} with platform {}", env.name, env.platform)

    val connectDetails: DbConnectDetails = try {
      when (env.platform) {
        "oracle" -> OracleConnectDetails(
          name = env.name!!,
          user = env.controlSchema!!,
          pw = env.pass!!,
          ldap = env.ldap!!,
          poolSize = poolSize
        )
        "postgresql" -> PostgresConnectDetails(
          name = env.name!!,
          user = env.controlSchema!!,
          pw = env.pass!!,
          host = env.host!!,
          port = postgresPort,
          poolSize = poolSize,
          pgDbName = env.pgDbName!!
        )
        else -> OracleConnectDetails(
          name = env.name!!,
          user = env.controlSchema!!,
          pw = env.pass!!,
          ldap = env.ldap!!,
          poolSize = poolSize
        )
      }
    } catch (e: Throwable) {
      throw IllegalStateException("error encountered while attempting to create a JDBC connection to ${env.name}", e)
    }

      dataSources[env.name!!] = AppDBRegistryEntry(
          env.name!!,
          connectDetails.findHost(),
          connectDetails.findPort(),
          connectDetails.makeDataSource(),
          env.dataSchema!!,
          env.controlSchema!!,
          env.platform ?: "oracle"
      )
  }

  interface DbConnectDetails {
      fun makeDataSource(): DataSource
      fun findHost(): String
      fun findPort(): UShort
  }

  class OracleConnectDetails(
    val name: String,
    val user: String,
    val pw: SecretString,
    val ldap: String,
    val poolSize: Int
  ) : DbConnectDetails {

    val desc: OracleNetDesc by lazy { LDAP.requireSingularOracleNetDesc(ldap) }

    override fun makeDataSource(): DataSource {
      return HikariConfig()
        .apply {
          jdbcUrl = makeJDBCOracleConnectionString(desc.host, desc.port, desc.serviceName)
          username = user
          password = pw.unwrap()
          maximumPoolSize = poolSize
          driverClassName = OracleDriver::class.java.name
        }
        .let(::HikariDataSource)
    }

    override fun findHost(): String {
      return desc.host
    }

    override fun findPort(): UShort {
      return desc.port
    }
  }

  class PostgresConnectDetails(
    val name: String,
    val user: String,
    val pw: SecretString,
    val host: String,
    val port: UShort,
    val poolSize: Int,
    val pgDbName: String
  ) : DbConnectDetails {

    override fun makeDataSource(): DataSource {
      log.info("Making data source with connection string {}", makeJDBCPostgresConnectionString(host, port, pgDbName))
      return HikariConfig()
        .apply {
          jdbcUrl = makeJDBCPostgresConnectionString(host, port, pgDbName)
          username = user
          password = pw.unwrap()
          maximumPoolSize = poolSize
          driverClassName = org.postgresql.Driver::class.java.name
        }
        .let(::HikariDataSource)
    }

    override fun findHost(): String {
      return host
    }

    override fun findPort(): UShort {
      return port
    }
  }

  private fun makeJDBCOracleConnectionString(host: String, port: UShort, name: String) =
    "jdbc:oracle:thin:@//$host:$port/$name"

  private fun makeJDBCPostgresConnectionString(host: String, port: UShort, name: String) =
    "jdbc:postgresql://$host:$port/$name"

}