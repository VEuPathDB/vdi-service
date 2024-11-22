package vdi.component.db.app

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import oracle.jdbc.OracleDriver
import org.slf4j.LoggerFactory
import org.veupathdb.lib.ldap.OracleNetDesc
import org.veupathdb.vdi.lib.common.env.DBEnvGroup
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.SecretString
import vdi.component.env.EnvKey
import vdi.component.ldap.LDAP
import javax.sql.DataSource

@Suppress("NOTHING_TO_INLINE")
object AppDatabaseRegistry {

  private val log = LoggerFactory.getLogger(javaClass)

  private val dataSources = HashMap<String, AppDBRegistryCollection>(12)

  var isBroken = false
    private set

  init {
    try {
      init(System.getenv())
    } catch (e: Throwable) {
      isBroken = true
      throw e
    }
  }

  fun contains(key: String, dataType: DataType): Boolean = get(key, dataType) != null

  fun contains(key: String): Boolean = key in dataSources

  operator fun get(key: String, dataType: DataType): AppDBRegistryEntry? = dataSources[key]?.get(dataType)

  operator fun get(key: String): AppDBRegistryCollection? = dataSources[key]

  /**
   * Iterates over all known app database registry entries as pairs of
   * DB/project name to details.
   *
   * **CAUTION**: The DB/project name values may not be unique!  Multiple
   * database entries may have the same name.
   */
  operator fun iterator() = asSequence().iterator()

  fun size() = dataSources.size

  /**
   * Returns a sequence over all known app database registry entries as pairs of
   * DB/project name to details.
   *
   * **CAUTION**: The DB/project name values may not be unique!  Multiple
   * database entries may have the same name.
   */
  fun asSequence() =
    dataSources.entries
      .asSequence()
      .map { (key, value) -> key to value }
      .flatMap { (dbName, values) -> values.map { (dataType, value) -> RegisteredAppDatabase(dbName, dataType, value) } }

  fun require(key: String, dataType: DataType): AppDBRegistryEntry =
    get(key, dataType)
      ?: throw IllegalStateException("required AppDB connection $key was not registered with AppDatabases")

  internal fun init(env: Environment) {
    val builders = HashMap<String, AppDBRegistryCollection.Builder>()

    DBEnvGroup.fromEnvironment(env, EnvKey.AppDB.DBConnectionDataTypes)
      .onEach { if (it.enabled == true) requireFullChunk(it) }
      .forEach { parseChunk(it, builders) }

    dataSources.clear()
    builders.forEach { (k, v) -> dataSources[k] = v.build() }
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

  private fun parseChunk(env: DBEnvGroup, builders: MutableMap<String, AppDBRegistryCollection.Builder>) {
    if (env.enabled != true) {
      log.info("Database {} is marked as disabled, skipping.", env.connectionName)
      return
    }

    log.info(
      """registering database {} with the following details:
      Connection Name: {}
      TNS: {}
      Plugin Restrictions: {}
      Host: {}
      Port: {}
      DB Name: {}
      User/Schema: {}
      Pool Size: {}""",
      env.connectionName,
      env.connectionName,
      env.ldap,
      env.extensions[EnvKey.AppDB.DBConnectionDataTypes],
      env.host,
      env.port,
      env.dbName,
      env.controlSchema,
      env.poolSize ?: DefaultPoolSize,
    )

    val platform = env.platform?.let { AppDBPlatform.fromString(it) } ?: AppDBPlatform.Oracle

    log.info("constructing a DataSource for database {} with platform {}", env.connectionName, platform)

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

    builders.computeIfAbsent(env.connectionName!!) {
      AppDBRegistryCollection.Builder()
        .apply {
          val entry = AppDBRegistryEntry(
            env.connectionName!!,
            connectDetails.findHost(),
            connectDetails.findPort(),
            connectDetails.makeDataSource(),
            env.dataSchema!!,
            env.controlSchema!!,
            platform
          )

          when (val dt = env.extensions[EnvKey.AppDB.DBConnectionDataTypes]) {
            null -> put(entry)
            "*"  -> put(entry)
            else -> {
              val plugins = dt.splitToSequence(',')
                .filter { it.isNotBlank() }
                .map { it.trim() }
                .toSet()

              if (plugins.isEmpty()) {
                put(entry)
              } else if (plugins.contains("*")) {
                if (plugins.size == 1)
                  put(entry)
                else
                  throw IllegalStateException("Database connection config set for ${env.connectionName} specifies that it is both a fallback and data type restricted")
              } else {
                for (plugin in plugins)
                  put(DataType.of(plugin), entry)
              }
            }
          }
        }
    }
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

