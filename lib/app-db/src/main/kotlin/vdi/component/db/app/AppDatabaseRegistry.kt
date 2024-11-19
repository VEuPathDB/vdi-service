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

  operator fun iterator() = asSequence().iterator()

  fun size() = dataSources.size

  fun asSequence() =
    dataSources.entries
      .asSequence()
      .map { (key, value) -> key to value }

  fun require(key: String): AppDBRegistryEntry =
    get(key) ?: throw IllegalStateException("required AppDB connection $key was not registered with AppDatabases")

  internal fun init(env: Environment) {
    dataSources.clear()

    DBEnvGroup.fromEnvironment(env)
      .onEach { if (it.enabled == true) requireFullChunk(it) }
      .forEach { parseChunk(it) }
  }

  private inline fun throwForMissingVar(envVar: String): Nothing =
    throw IllegalStateException("one or more app database definition blocks in the service environment is missing its '$envVar' value")

  private fun requireFullChunk(db: DBEnvGroup) {
    db.connectionName ?: throwForMissingVar(EnvKey.AppDB.DBConnectionNamePrefix)
    db.pass           ?: throwForMissingVar(EnvKey.AppDB.DBPassPrefix)
    db.controlSchema  ?: throwForMissingVar(EnvKey.AppDB.DBControlSchemaPrefix)
    db.dataSchema     ?: throwForMissingVar(EnvKey.AppDB.DBDataSchemaPrefix)

    if (db.ldap == null) {
      db.dbName ?: throwForMissingVar(EnvKey.AppDB.DBNamePrefix)
      db.host   ?: throwForMissingVar(EnvKey.AppDB.DBHostPrefix)
      db.port   ?: throwForMissingVar(EnvKey.AppDB.DBPortPrefix)
    } else if (db.platform != null && AppDBPlatform.fromString(db.platform!!) == AppDBPlatform.Postgres) {
      throw IllegalStateException("LDAP lookup is currently unsupported for postgres app databases")
    }
  }

  private fun parseChunk(env: DBEnvGroup) {
    if (env.enabled != true) {
      log.info("Database {} is marked as disabled, skipping.", env.connectionName)
      return
    }

    log.info(
      """registering database {} with the following details:
      Connection Name: {}
      TNS: {}
      Host: {}
      Port: {}
      DB Name: {}
      User/Schema: {}
      Pool Size: {}""",
      env.connectionName,
      env.connectionName,
      env.ldap,
      env.host,
      env.port,
      env.dbName,
      env.controlSchema,
      env.poolSize ?: DefaultPoolSize,
    )

    val platform = env.platform?.let { AppDBPlatform.fromString(it) } ?: AppDBPlatform.Oracle

    log.info("constructing a DataSource for database {} with platform {}", env.connectionName, env.platform)

    val connectDetails: DbConnectDetails = try {
      when (platform) {
        AppDBPlatform.Oracle ->
          if (env.ldap == null)
            ManualConnectDetails(
              name     = env.connectionName!!,
              user     = env.controlSchema!!,
              pw       = env.pass!!,
              host     = env.host!!,
              port     = env.port!!,
              dbName   = env.dbName!!,
              poolSize = (env.poolSize ?: DefaultPoolSize).toInt(),
            )
          else
            LDAPConnectDetails(
              name     = env.connectionName!!,
              user     = env.controlSchema!!,
              pw       = env.pass!!,
              ldap     = env.ldap!!,
              poolSize = (env.poolSize ?: DefaultPoolSize).toInt(),
            )
        AppDBPlatform.Postgres -> ManualConnectDetails(
          name     = env.connectionName!!,
          user     = env.controlSchema!!,
          pw       = env.pass!!,
          host     = env.host!!,
          port     = env.port!!,
          dbName   = env.dbName!!,
          poolSize = (env.poolSize ?: DefaultPoolSize).toInt(),
        )
      }
    } catch (e: Throwable) {
      throw IllegalStateException("error encountered while attempting to create a JDBC connection for ${env.connectionName}", e)
    }

    dataSources[env.connectionName!!] = AppDBRegistryEntry(
      env.connectionName!!,
      connectDetails.findHost(),
      connectDetails.findPort(),
      connectDetails.makeDataSource(),
      env.dataSchema!!,
      env.controlSchema!!,
      platform
    )
  }

  interface DbConnectDetails {
    fun makeDataSource(): DataSource
    fun findHost(): String
    fun findPort(): UShort
  }

  class LDAPConnectDetails(
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

  class ManualConnectDetails(
    val name: String,
    val user: String,
    val pw: SecretString,
    val host: String,
    val port: UShort,
    val dbName: String,
    val poolSize: Int,
  ) : DbConnectDetails {

    override fun makeDataSource(): DataSource {
      log.info("Making data source with connection string {}", makeJDBCPostgresConnectionString(host, port, dbName))
      return HikariConfig()
        .apply {
          jdbcUrl = makeJDBCPostgresConnectionString(host, port, dbName)
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
