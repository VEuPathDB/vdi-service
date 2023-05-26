package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import java.sql.Connection

// language=oracle
private const val SQL = """
INSERT INTO
  vdi.dataset_project (
    dataset_id
  , project_id
  )
VALUES
  (?, ?)
"""

internal fun Connection.insertDatasetProjectLinks(datasetID: DatasetID, projectIDs: Iterable<ProjectID>) {
  prepareStatement(SQL)
    .use { ps ->
      for (projectID in projectIDs) {
        ps.setString(1, datasetID.toString())
        ps.setString(2, projectID)
        ps.addBatch()
      }

      ps.executeBatch()
    }
}