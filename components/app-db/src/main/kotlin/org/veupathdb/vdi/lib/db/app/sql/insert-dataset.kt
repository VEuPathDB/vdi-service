package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.db.app.model.DatasetRecord
import java.sql.Connection

// language=oracle
private const val SQL = """
INSERT INTO
  vdi.datasets (
    dataset_id
  , owner
  , type_name
  , type_version
  , is_deleted
  )
VALUES
  (?, ?, ?, ?, ?)
"""

internal fun Connection.insertDataset(dataset: DatasetRecord) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, dataset.datasetID.toString())
      ps.setLong(2, dataset.owner.toLong())
      ps.setString(3, dataset.typeName)
      ps.setString(4, dataset.typeVersion)
      ps.setBoolean(5, dataset.isDeleted)
      ps.executeUpdate()
    }
}