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
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setLong(1, dataset.owner.toLong())
      ps.setString(2, dataset.typeName)
      ps.setString(3, dataset.typeVersion)
      ps.setInt(4, dataset.isDeleted.value)
      ps.setBoolean(5, dataset.isPublic)
      ps.setString(6, dataset.datasetID.toString())
      ps.executeUpdate()
    }
}