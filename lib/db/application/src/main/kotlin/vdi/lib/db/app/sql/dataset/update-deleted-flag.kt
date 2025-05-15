package vdi.lib.db.app.sql.dataset

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import vdi.lib.db.app.model.DeleteFlag
import vdi.lib.db.app.sql.setDeleteFlag
import vdi.lib.db.jdbc.setDatasetID

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
