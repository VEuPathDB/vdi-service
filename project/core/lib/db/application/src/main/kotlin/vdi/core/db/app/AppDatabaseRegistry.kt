package vdi.core.db.app

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import vdi.core.db.app.health.DatabaseDependency
import vdi.core.health.RemoteDependencies
import vdi.db.app.InstallTargetRegistry
import vdi.db.app.TargetDBPlatform
import vdi.db.app.TargetDatabaseDetails
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID

object AppDatabaseRegistry {
  private val dataSources: Map<InstallTargetID, AppDBRegistryCollection>

  init {
    val builders = HashMap<InstallTargetID, MutableMap<DatasetType, TargetDatabaseReference>>(16)

    InstallTargetRegistry.asSequence()
      .forEach { (target, type, config) ->
        val targetDB = TargetDatabaseReference(
          identifier = target,
          details    = config.controlDatabase,
          dataSource = config.controlDatabase.makeDataSource()
        )

        builders.computeIfAbsent(target) { HashMap(2) }[type] = targetDB

        RemoteDependencies.register(DatabaseDependency(targetDB))
      }

    dataSources = HashMap<InstallTargetID, AppDBRegistryCollection>(builders.size)
      .also { src -> builders.forEach { (k, v) ->
        src[k] = AppDBRegistryCollection(HashMap<DatasetType, TargetDatabaseReference>(v.size).apply { putAll(v) })
      } }
  }

  fun contains(key: InstallTargetID, dataType: DatasetType): Boolean = get(key, dataType) != null

  operator fun get(key: InstallTargetID, dataType: DatasetType): TargetDatabaseReference? = dataSources[key]?.get(dataType)

  operator fun get(key: InstallTargetID): AppDBRegistryCollection? = dataSources[key]

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

  fun require(key: InstallTargetID, dataType: DatasetType): TargetDatabaseReference =
    get(key, dataType)
      ?: throw IllegalStateException("required AppDB connection $key was not registered with AppDatabases")

  private fun TargetDatabaseDetails.makeDataSource() =
    HikariConfig()
      .also {
        it.jdbcUrl = toJDBCString()
        it.username = user
        it.password = pass.asString
        it.maximumPoolSize = poolSize.toInt()
        it.driverClassName = driverClass
      }
      .let(::HikariDataSource)

  private fun TargetDatabaseDetails.toJDBCString() =
    when (platform) {
      TargetDBPlatform.Postgres -> "jdbc:postgresql://$server/$name"
      TargetDBPlatform.Oracle   -> "jdbc:oracle:thin:@//$server/$name"
    }

  private inline val TargetDatabaseDetails.driverClass get() =
    when (platform) {
      TargetDBPlatform.Postgres -> "org.postgresql.Driver"
      TargetDBPlatform.Oracle   -> "oracle.jdbc.OracleDriver"
    }
}
