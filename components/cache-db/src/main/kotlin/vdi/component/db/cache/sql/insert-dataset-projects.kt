package vdi.component.db.cache.sql

import java.sql.Connection
import vdi.components.common.fields.DatasetID

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_projects (
    dataset_id
  , project_id
  )
VALUES
  (?, ?)
"""

fun Connection.insertDatasetProjects(datasetID: DatasetID, projects: Iterable<String>) {
  prepareStatement(SQL).use { ps ->
    for (project in projects) {
      ps.setString(1, datasetID.toString())
      ps.setString(2, project)
      ps.addBatch()
    }

    ps.executeBatch()
  }
}