package vdi.core.db.app.sql.dataset_project

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_project
WHERE
  dataset_id = ?
  AND project_id = ?
"""

internal fun Connection.deleteDatasetProjectLink(schema: String, datasetID: DatasetID, installTarget: InstallTargetID) =
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, installTarget)
  }
