package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import java.sql.Connection

// language=oracle
private const val SQL = """
DELETE FROM
  vdi.dataset_projects
WHERE
  dataset_id = ?
  AND project_id = ?
"""

internal fun Connection.deleteDatasetProjectLink(datasetID: DatasetID, projectID: ProjectID): Int =
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, projectID)
      ps.executeUpdate()
    }