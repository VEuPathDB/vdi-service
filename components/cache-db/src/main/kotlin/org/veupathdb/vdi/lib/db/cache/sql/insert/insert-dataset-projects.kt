package org.veupathdb.vdi.lib.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_projects (
    dataset_id
  , project_id
  )
VALUES
  (?, ?)
ON CONFLICT (dataset_id, project_id)
  DO NOTHING
"""

internal fun Connection.tryInsertDatasetProjects(datasetID: DatasetID, projects: Iterable<String>) {
  prepareStatement(SQL).use { ps ->
    for (project in projects) {
      ps.setString(1, datasetID.toString())
      ps.setString(2, project)
      ps.addBatch()
    }

    ps.executeBatch()
  }
}