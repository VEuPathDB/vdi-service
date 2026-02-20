package vdi.core.db.app

import javax.sql.DataSource
import vdi.core.config.loadAndCacheStackConfig
import vdi.core.db.app.health.DatabaseDependency
import vdi.core.health.RemoteDependencies
import vdi.db.app.InstallTargetRegistry
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID

object AppDatabaseRegistry {
  private val dataSources: Map<InstallTargetID, AppDBRegistryCollection>

  init {
    val builders = HashMap<InstallTargetID, MutableMap<DatasetType, TargetDatabaseReference>>(16)
    val distinctDatabaseReferences = HashMap<TargetDatabaseConfig, Unit>(16)

    val sharedPoolConfig = loadAndCacheStackConfig().vdi.sharedTargetDbPooling

    // toList to evaluate stream and resolve all db refs
    val targetDatabases = InstallTargetRegistry.asSequence()
      .map { (target, type, config) ->
        val dbRef = TargetDatabaseConfig(config.controlDatabase)

        distinctDatabaseReferences.compute(dbRef) { k, v ->
          // If we have multiple targets sharing a db reference
          if (v != null) {
            k.idleTimeout = sharedPoolConfig.idleTimeout
            k.poolSize    = sharedPoolConfig.poolSize
            dbRef.idleTimeout = sharedPoolConfig.idleTimeout
            dbRef.poolSize    = sharedPoolConfig.poolSize
          }
        }

        Quad(target, type, config, dbRef)
      }
      .toList()

    val sharedDatabaseRefs = HashMap<TargetDatabaseConfig, DataSource>(targetDatabases.size)

    targetDatabases.forEach { (target, type, config, dbRef) ->
      val targetDB = TargetDatabaseReference(
        identifier = target,
        details    = config.controlDatabase,
        dataSource = sharedDatabaseRefs.computeIfAbsent(dbRef) {
          dbRef.makeDataSource()
        }
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
}
