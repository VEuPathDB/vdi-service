package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import java.sql.Connection

// language=oracle
private const val SQL = """
SELECT
  1
FROM
  vdi.dataset_projects
WHERE
  dataset_id = ?
  AND project_id = ?
"""

internal fun Connection.testDatasetProjectLinkExists(datasetID: DatasetID, projectID: ProjectID): Boolean =
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, projectID)
      ps.executeQuery()
        .use { rs -> rs.next() }
    }