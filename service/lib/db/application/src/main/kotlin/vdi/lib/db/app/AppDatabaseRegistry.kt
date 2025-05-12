package vdi.lib.db.app

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import oracle.jdbc.OracleDriver
import org.veupathdb.lib.ldap.NetDesc
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.SecretString
import javax.sql.DataSource
import vdi.lib.config.common.DirectDatabaseConnectionConfig
import vdi.lib.config.common.LDAPDatabaseConnectionConfig
import vdi.lib.config.loadAndCacheStackConfig
import vdi.lib.config.vdi.InstallTargetConfig
import vdi.lib.db.app.health.DatabaseDependency
import vdi.lib.health.RemoteDependencies
import vdi.lib.ldap.LDAP
import vdi.lib.logging.logger

object AppDatabaseRegistry {
  private val dataSources = HashMap<ProjectID, AppDBRegistryCollection>(12)

  var isBroken = false
    private set

  init {
    try {
      val logger   = logger<AppDB>()
      val builders = HashMap<String, AppDBRegistryCollection.Builder>(16)
      val targets  = loadAndCacheStackConfig().vdi.installTargets

      targets.asSequence()
        .filter {
          it.enabled.also { enabled -> if (!enabled) logger.info("install target ${it.targetName} is disabled") }
        }
        .forEach { initTarget(it, builders) }

      builders.forEach { (k, v) -> dataSources[k] = v.build() }
    } catch (e: Throwable) {
      isBroken = true
      throw e
    }
  }

  private fun initTarget(config: InstallTargetConfig, builders: MutableMap<String, AppDBRegistryCollection.Builder>) {
    val details = when (val db = config.controlDB) {
      is DirectDatabaseConnectionConfig -> {
        val platform = AppDBPlatform.fromString(db.platform)
        ManualConnectDetails(
          db.username,
          db.password,
          db.server.host,
          db.server.port ?: platform.port,
          db.dbName,
          platform,
          db.poolSize ?: DefaultPoolSize,
        )
      }
      is LDAPDatabaseConnectionConfig -> {
        LDAPConnectDetails(
          db.username,
          db.password,
          db.lookupCN,
          db.poolSize ?: DefaultPoolSize,
        )
      }
    }

    builders.computeIfAbsent(config.targetName) { AppDBRegistryCollection.Builder() }
      .apply {
        val entry = AppDBRegistryEntry(
          config.targetName,
          details.findHost(),
          details.findPort(),
          details.makeDataSource(),
          config.controlDB.username,
          details.platform
        )

        when (config.dataTypes.size) {
          0 -> put(entry)
          1 -> when (val dt = config.dataTypes.first()) {
            "*"  -> put(entry)
            else -> put(DataType.of(dt), entry)
          }
          else -> config.dataTypes.forEach { put(DataType.of(it), entry) }
        }

        RemoteDependencies.register(DatabaseDependency(entry))
      }
  }

  fun contains(key: ProjectID, dataType: DataType): Boolean = get(key, dataType) != null

  operator fun get(key: ProjectID, dataType: DataType): AppDBRegistryEntry? = dataSources[key]?.get(dataType)

  operator fun get(key: ProjectID): AppDBRegistryCollection? = dataSources[key]

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

  fun require(key: ProjectID, dataType: DataType): AppDBRegistryEntry =
    get(key, dataType)
      ?: throw IllegalStateException("required AppDB connection $key was not registered with AppDatabases")
}

private interface DbConnectDetails {
  val platform: AppDBPlatform
  fun makeDataSource(): DataSource
  fun findHost(): String
  fun findPort(): UShort
}

private class ManualConnectDetails(
  val user: String,
  val pw: SecretString,
  val host: String,
  val port: UShort,
  val dbName: String,
  override val platform: AppDBPlatform,
  val poolSize: UByte,
) : DbConnectDetails {

  override fun makeDataSource(): DataSource {
    return HikariConfig()
      .apply {
        jdbcUrl = when (platform) {
          AppDBPlatform.Oracle   -> makeJDBCOracleConnectionString(host, port, dbName)
          AppDBPlatform.Postgres -> makeJDBCPostgresConnectionString(host, port, dbName)
        }
        username = user
        password = pw.unwrap()
        maximumPoolSize = poolSize.toInt()
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

private class LDAPConnectDetails(
  val user: String,
  val pw: SecretString,
  val ldap: String,
  val poolSize: UByte
) : DbConnectDetails {

  val desc: NetDesc by lazy { LDAP.requireSingularNetDesc(ldap) }

  override val platform
    get() = AppDBPlatform.fromString(desc.platform.name)

  override fun makeDataSource(): DataSource {
    return HikariConfig()
      .apply {
        jdbcUrl = when (platform) {
          AppDBPlatform.Oracle   -> makeJDBCOracleConnectionString(desc.host, desc.port.toUShort(), desc.identifier)
          AppDBPlatform.Postgres -> makeJDBCPostgresConnectionString(desc.host, desc.port.toUShort(), desc.identifier)
        }
        username = user
        password = pw.unwrap()
        maximumPoolSize = poolSize.toInt()
        driverClassName = OracleDriver::class.java.name
      }
      .let(::HikariDataSource)
  }

  override fun findHost(): String {
    return desc.host
  }

  override fun findPort(): UShort {
    return desc.port.toUShort()
  }
}

private fun makeJDBCOracleConnectionString(host: String, port: UShort, name: String) =
  "jdbc:oracle:thin:@//$host:$port/$name"

private fun makeJDBCPostgresConnectionString(host: String, port: UShort, name: String) =
  "jdbc:postgresql://$host:$port/$name"
