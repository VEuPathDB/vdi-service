package vdi.core.db.app.sql.dataset_visibility

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID
import vdi.model.data.DatasetID
import vdi.model.data.UserID

private fun sql(schema: String) =
// language=postgresql
"""
INSERT INTO
  ${schema}.dataset_visibility (
    dataset_id
  , user_id
  )
VALUES
  (?, ?)
"""

internal fun Connection.insertDatasetVisibility(schema: String, datasetID: DatasetID, userID: UserID) {
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setUserID(2, userID)
  }
}
