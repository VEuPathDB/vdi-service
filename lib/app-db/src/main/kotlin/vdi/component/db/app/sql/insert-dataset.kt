package vdi.component.db.app.sql

import vdi.component.db.app.model.DatasetRecord
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.dataset (
    dataset_id
  , owner
  , type_name
  , type_version
  , is_deleted
  , is_public
  )
VALUES
  (?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDataset(schema: String, dataset: DatasetRecord) {
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, dataset.datasetID.toString())
      ps.setLong(2, dataset.owner.toLong())
      ps.setString(3, dataset.typeName.lowercase())
      ps.setString(4, dataset.typeVersion)
      ps.setInt(5, dataset.isDeleted.value)
      ps.setBoolean(6, dataset.isPublic)
      ps.executeUpdate()
    }
}