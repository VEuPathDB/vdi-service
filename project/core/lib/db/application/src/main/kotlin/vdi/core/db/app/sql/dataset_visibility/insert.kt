package vdi.core.db.app.sql.dataset_visibility

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.app.sql.setUserID
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID

private fun sql(schema: String) =
// language=sql
"""
INSERT INTO
  ${schema}.${Table.Visibility} (
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
