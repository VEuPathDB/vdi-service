package vdi.component.db.app.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.jdbc.setDatasetID
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
  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, projectID)
    withResults { next() }
  }
