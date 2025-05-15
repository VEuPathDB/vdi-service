package vdi.lib.db.app.sql.dataset

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.lib.db.app.model.DatasetRecord
import vdi.lib.db.app.sql.setDeleteFlag
import vdi.lib.db.jdbc.setDataType
import vdi.lib.db.jdbc.setDatasetID
import vdi.lib.db.jdbc.setUserID

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
    setDataType(2, dataset.typeName)
    setString(3, dataset.typeVersion)
    setDeleteFlag(4, dataset.deletionState)
    setBoolean(5, dataset.isPublic)
    setDatasetID(6, dataset.datasetID)
    setString(7, dataset.accessibility.value)
    setInt(8, dataset.daysForApproval)
  }
}
