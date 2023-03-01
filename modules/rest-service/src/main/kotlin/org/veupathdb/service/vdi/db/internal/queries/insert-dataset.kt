package org.veupathdb.service.vdi.db.internal.queries

import java.sql.Connection
import vdi.components.common.util.fields.DatasetID

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.datasets (
    dataset_id
  , type_name
  , type_version
  , owner_id
  , is_deleted
  , created
  )
VALUES
  (?, ?, ?, ?, FALSE, now())
"""

fun Connection.insertDatasetRecord(
  datasetID: DatasetID,
  typeName: String,
  typeVersion: String,
  ownerID: Long,
) {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.value)
    ps.setString(2, typeName)
    ps.setString(3, typeVersion)
    ps.setString(4, ownerID.toString())

    ps.execute()
  }
}