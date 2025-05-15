package vdi.lib.db.app.sql.dataset_visibility

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID
import vdi.lib.db.jdbc.setUserID

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_visibility
WHERE
  dataset_id = ?
  AND user_id = ?
"""

internal fun Connection.deleteDatasetVisibility(schema: String, datasetID: DatasetID, userID: UserID) =
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setUserID(2, userID)
  }
