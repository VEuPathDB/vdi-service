package vdi.component.db.app.sql

import vdi.component.db.app.model.DatasetRecord
import java.sql.Connection

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
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDataset(schema: String, dataset: DatasetRecord) {
  preparedUpdate(sql(schema)) {
    setUserID(1, dataset.owner)
    setDataType(2, dataset.typeName)
    setString(3, dataset.typeVersion)
    setDeleteFlag(4, dataset.isDeleted)
    setBoolean(5, dataset.isPublic)
    setDatasetID(6, dataset.datasetID)
  }
}
