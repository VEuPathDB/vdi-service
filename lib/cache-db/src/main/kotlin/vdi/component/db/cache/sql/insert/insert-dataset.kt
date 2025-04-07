package vdi.component.db.cache.sql.insert

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.component.db.cache.model.Dataset
import vdi.component.db.jdbc.setDataType
import vdi.component.db.jdbc.setDatasetID
import vdi.component.db.jdbc.setUserID
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.datasets (
    dataset_id
  , type_name
  , type_version
  , owner_id
  , is_deleted
  , origin
  , created
  , inserted
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id)
  DO NOTHING
"""

internal fun Connection.tryInsertDatasetRecord(row: Dataset) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, row.datasetID)
    setDataType(2, row.typeName)
    setString(3, row.typeVersion)
    setUserID(4, row.ownerID)
    setBoolean(5, row.isDeleted)
    setString(6, row.origin)
    setObject(7, row.created)
    setObject(8, row.inserted)
  }
