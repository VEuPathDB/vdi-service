package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_project
WHERE
  dataset_id = ?
  AND project_id = ?
"""

internal fun Connection.deleteDatasetProjectLink(schema: String, datasetID: DatasetID, projectID: ProjectID): Int =
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, projectID)
      ps.executeUpdate()
    }