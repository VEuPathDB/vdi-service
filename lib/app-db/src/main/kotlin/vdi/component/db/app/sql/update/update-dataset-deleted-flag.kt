package vdi.component.db.app.sql.update

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.DeleteFlag
import vdi.component.db.app.sql.setDeleteFlag
import vdi.component.db.jdbc.setDatasetID
import java.sql.Connection

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
