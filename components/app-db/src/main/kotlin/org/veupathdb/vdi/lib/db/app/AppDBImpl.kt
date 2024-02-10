package org.veupathdb.vdi.lib.db.app

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.db.app.model.InstallStatuses
import org.veupathdb.vdi.lib.db.app.sql.selectInstallStatuses

internal object AppDBImpl : AppDB {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun getDatasetStatuses(targets: Map<ProjectID, Collection<DatasetID>>): Map<DatasetID, Map<ProjectID, InstallStatuses>> {
    log.trace("getDatasetStatuses(targets={})", targets)

    val result = HashMap<DatasetID, MutableMap<ProjectID, InstallStatuses>>(100)

    for ((projectID, datasetIDs) in targets) {
      val ds = AppDatabaseRegistry.require(projectID)

      ds.source.connection.use { con ->
        con.selectInstallStatuses(ds.ctlSchema, datasetIDs)
          .forEach { (datasetID, statuses) ->
            result.computeIfAbsent(datasetID) { HashMap() } [projectID] = statuses
          }
      }
    }

    return result
  }

  override fun getDatasetStatuses(target: DatasetID, projects: Collection<ProjectID>): Map<ProjectID, InstallStatuses> {
    log.trace("getDatasetStatuses(target={}, projects={})", target, projects)

    val out = HashMap<ProjectID, InstallStatuses>(projects.size)

    for (projectID in projects) {
      val ds = AppDatabaseRegistry.require(projectID)

      ds.source.connection.use { con -> out[projectID] = con.selectInstallStatuses(ds.ctlSchema, target) }
    }

    return out
  }

  override fun accessor(key: ProjectID): AppDBAccessor? =
    AppDatabaseRegistry[key]
      ?.let { AppDBAccessorImpl(it.ctlSchema, it.source) }

  override fun transaction(key: ProjectID): AppDBTransaction? =
    AppDatabaseRegistry[key]
      ?.let { ds -> AppDBTransactionImpl(ds.ctlSchema, ds.source.connection.also { it.autoCommit = false }) }
}

