package vdi.component.db.app.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.app.sql.preparedUpdate
import vdi.component.db.app.sql.setDatasetID
import vdi.component.db.app.sql.setUserID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
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
  preparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setUserID(2, userID)
  }
}
