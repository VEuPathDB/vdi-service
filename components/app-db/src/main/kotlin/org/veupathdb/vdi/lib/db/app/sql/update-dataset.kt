package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.db.app.model.DatasetRecord
import java.sql.Connection

// language=oracle
private const val SQL = """
UPDATE
  vdi.datasets
SET
  owner = ?
, type_name = ?
, type_version = ?
, is_deleted = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDataset(dataset: DatasetRecord) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setLong(1, dataset.owner.toLong())
      ps.setString(2, dataset.typeName)
      ps.setString(3, dataset.typeVersion)
      ps.setBoolean(4, dataset.isDeleted)
      ps.setString(5, dataset.datasetID.toString())
      ps.executeUpdate()
    }
}