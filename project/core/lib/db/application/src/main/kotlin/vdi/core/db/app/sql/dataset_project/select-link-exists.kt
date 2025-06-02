package vdi.core.db.app.sql.dataset_project

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID

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
  installTarget: InstallTargetID
): Boolean =
  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, installTarget)
    withResults { next() }
  }
