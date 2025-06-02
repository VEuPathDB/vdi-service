package vdi.core.db.app.sql.dataset

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.model.DeleteFlag
import vdi.core.db.app.sql.setDeleteFlag
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID

private fun sql(schema: String) =
// language=oracle
"""
UPDATE
  ${schema}.dataset
SET
  is_deleted = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetDeletedFlag(schema: String, datasetID: DatasetID, deleteFlag: DeleteFlag) {
  withPreparedUpdate(sql(schema)) {
    setDeleteFlag(1, deleteFlag)
    setDatasetID(2, datasetID)
  }
}
