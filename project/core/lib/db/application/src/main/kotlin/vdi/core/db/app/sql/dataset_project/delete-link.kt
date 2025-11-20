package vdi.core.db.app.sql.dataset_project

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID
import vdi.model.meta.InstallTargetID

private fun sql(schema: String) =
// language=sql
"""
DELETE FROM
  ${schema}.${Table.Projects}
WHERE
  dataset_id = ?
  AND project_id = ?
"""

internal fun Connection.deleteDatasetProjectLink(schema: String, datasetID: DatasetID, installTarget: InstallTargetID) =
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, installTarget)
  }
