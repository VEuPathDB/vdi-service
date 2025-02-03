package vdi.component.db.app.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.app.sql.setDatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
SELECT
  1
FROM
  ${schema}.dataset_project
WHERE
  dataset_id = ?
  AND project_id = ?
"""

internal fun Connection.testDatasetProjectLinkExists(
  schema: String,
  datasetID: DatasetID,
  projectID: ProjectID
): Boolean =
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setDatasetID(1, datasetID)
      ps.setString(2, projectID)
      ps.executeQuery().use { rs -> rs.next() }
    }
