package org.veupathdb.service.vdi.db

import org.veupathdb.service.vdi.db.appdb.selectInstallStatuses
import org.veupathdb.service.vdi.model.InstallStatuses
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID


object AppDB {
  fun getDatasetStatuses(targets: Map<ProjectID, Collection<DatasetID>>): Map<DatasetID, Map<ProjectID, InstallStatuses>> {
    val result = HashMap<DatasetID, MutableMap<ProjectID, InstallStatuses>>(100)

    for ((projectID, datasetIDs) in targets) {
      val ds = AppDatabases[projectID] ?: throw IllegalStateException("missing application database entry for project ID \"$projectID\"")

      ds.connection.use { con ->
        con.selectInstallStatuses(datasetIDs)
          .forEach { (datasetID, statuses) ->
            result.computeIfAbsent(datasetID) { HashMap() } [projectID] = statuses
          }
      }
    }

    return result
  }

  fun getDatasetStatuses(target: DatasetID, projects: Collection<ProjectID>): Map<ProjectID, InstallStatuses> {
    val out = HashMap<ProjectID, InstallStatuses>(projects.size)

    for (projectID in projects) {
      val ds = AppDatabases[projectID] ?: throw IllegalStateException("missing application database entry for project ID \"$projectID\"")

      ds.connection.use { con -> out[projectID] = con.selectInstallStatuses(target) }
    }

    return out
  }
}

