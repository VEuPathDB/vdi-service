package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import java.sql.Connection

// language=oracle
private const val SQL = """
INSERT INTO
  vdi.dataset_projects (
    dataset_id
  , project_id
  )
VALUES
  (?, ?)
"""

internal fun Connection.insertDatasetProjectLink(datasetID: DatasetID, projectID: ProjectID) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, projectID)
      ps.executeUpdate()
    }
}