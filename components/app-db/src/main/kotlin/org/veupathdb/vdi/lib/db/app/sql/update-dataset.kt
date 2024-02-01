package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.db.app.model.DatasetRecord
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
      ps.setString(5, dataset.datasetID.toString())
      ps.executeUpdate()
    }
}