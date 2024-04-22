package vdi.component.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.util.setDatasetID
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
      ps.setDatasetID(1, datasetID)
      ps.setString(2, project)
      ps.addBatch()
    }

    ps.executeBatch()
  }
}