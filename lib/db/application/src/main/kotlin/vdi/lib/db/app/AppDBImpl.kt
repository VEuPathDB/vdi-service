package vdi.lib.db.app

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.lib.db.app.model.InstallStatuses
import vdi.lib.db.app.sql.dataset_install_message.selectInstallStatuses

internal object AppDBImpl : AppDB {
  override fun getDatasetStatuses(targets: Map<ProjectID, Collection<DatasetID>>): Map<DatasetID, Map<ProjectID, InstallStatuses>> {
    val result = HashMap<DatasetID, MutableMap<ProjectID, InstallStatuses>>(100)

    for ((projectID, datasetIDs) in targets) {
      for ((dataType, _) in AppDatabaseRegistry[projectID] ?: emptyList()) {
        val ds = AppDatabaseRegistry.require(projectID, dataType)

        ds.source.connection.use { con ->
          con.selectInstallStatuses(ds.ctlSchema, datasetIDs)
            .forEach { (datasetID, statuses) ->
              result.computeIfAbsent(datasetID) { HashMap() }[projectID] = statuses
            }
        }
      }
    }

    return result
  }

  override fun getDatasetStatuses(target: DatasetID, projects: Collection<ProjectID>): Map<ProjectID, InstallStatuses> {
    val out = HashMap<ProjectID, InstallStatuses>(projects.size)

    for (projectID in projects) {
      for ((dataType, _) in AppDatabaseRegistry[projectID] ?: emptyList()) {
        with(AppDatabaseRegistry.require(projectID, dataType)) {
          source.connection.use { con -> out[projectID] = con.selectInstallStatuses(ctlSchema, target) }
        }
      }
    }

    return out
  }

  override fun accessor(key: ProjectID, dataType: DataType): AppDBAccessor? =
    AppDatabaseRegistry[key, dataType]
      ?.let { AppDBAccessorImpl(key, it.ctlSchema, it.source, it.platform) }

  override fun transaction(key: ProjectID, dataType: DataType): AppDBTransaction? =
    AppDatabaseRegistry[key, dataType]
      ?.let { ds -> AppDBTransactionImpl(key, ds.ctlSchema, ds.source.connection.also { it.autoCommit = false }, ds.platform) }
}

