package vdi.core.db.app

import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.app.sql.dataset_install_message.selectInstallStatuses
import vdi.db.app.TargetDBPlatform
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID

internal object AppDBImpl : AppDB {
  override fun getDatasetStatuses(targets: Map<InstallTargetID, Collection<DatasetID>>): Map<DatasetID, Map<InstallTargetID, InstallStatuses?>> {
    val result = HashMap<DatasetID, MutableMap<InstallTargetID, InstallStatuses?>>(64)

    for ((projectID, datasetIDs) in targets) {
      for ((dataType, _) in AppDatabaseRegistry[projectID] ?: emptyList()) {
        val ds = AppDatabaseRegistry.require(projectID, dataType)

        ds.dataSource.connection.use { con ->
          val statuses = con.selectInstallStatuses(ds.details.schema, datasetIDs)

          datasetIDs.forEach { datasetID ->
            result.computeIfAbsent(datasetID) { HashMap(1) }[projectID] = statuses[datasetID]
          }
        }
      }
    }

    return result
  }

  override fun getDatasetStatuses(target: DatasetID, projects: Collection<InstallTargetID>): Map<InstallTargetID, InstallStatuses> {
    val out = HashMap<InstallTargetID, InstallStatuses>(projects.size)

    for (projectID in projects) {
      for ((dataType, _) in AppDatabaseRegistry[projectID] ?: emptyList()) {
        with(AppDatabaseRegistry.require(projectID, dataType)) {
          dataSource.connection.use { con -> out[projectID] = con.selectInstallStatuses(details.schema, target) }
        }
      }
    }

    return out
  }

  override fun accessor(key: InstallTargetID, dataType: DatasetType): AppDBAccessor? =
    AppDatabaseRegistry[key, dataType]
      ?.let { AppDBAccessorImpl(key, it.details.schema, it.dataSource, it.details.platform) }

  override fun transaction(key: InstallTargetID, dataType: DatasetType): AppDBTransaction? =
    AppDatabaseRegistry[key, dataType]
      ?.let { ds -> when (ds.details.platform) {
        TargetDBPlatform.Postgres -> PostgresTransaction(key, ds.details.schema, ds.dataSource.connection.also { it.autoCommit = false })
        TargetDBPlatform.Oracle -> OracleTransaction(key, ds.details.schema, ds.dataSource.connection.also { it.autoCommit = false })
      } }
}

