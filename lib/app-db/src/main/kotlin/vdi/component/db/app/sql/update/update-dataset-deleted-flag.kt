package vdi.component.db.app.sql.update

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.DeleteFlag
import vdi.component.db.app.sql.preparedUpdate
import vdi.component.db.app.sql.setDatasetID
import vdi.component.db.app.sql.setDeleteFlag
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
  preparedUpdate(sql(schema)) {
    setDeleteFlag(1, deleteFlag)
    setDatasetID(2, datasetID)
  }
}
