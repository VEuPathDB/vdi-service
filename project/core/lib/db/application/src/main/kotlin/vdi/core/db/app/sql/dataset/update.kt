package vdi.core.db.app.sql.dataset

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.sql.setDeleteFlag
import vdi.core.db.jdbc.setDataType
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID

private fun sql(schema: String) =
// language=oracle
"""
UPDATE
  ${schema}.dataset
SET
  owner = ?
, type_name = ?
, type_version = ?
, is_deleted = ?
, is_public = ?
, accessibility = ?
, days_for_approval = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDataset(schema: String, dataset: DatasetRecord) {
  withPreparedUpdate(sql(schema)) {
    setUserID(1, dataset.owner)
    setDataType(2, dataset.type.name)
    setString(3, dataset.type.version)
    setDeleteFlag(4, dataset.deletionState)
    setBoolean(5, dataset.isPublic)
    setDatasetID(6, dataset.datasetID)
    setString(7, dataset.accessibility.value)
    setInt(8, dataset.daysForApproval)
  }
}
