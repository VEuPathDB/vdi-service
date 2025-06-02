package vdi.core.db.cache.sql.insert

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.cache.model.Dataset
import vdi.core.db.jdbc.setDataType
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID

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
    setDataType(2, row.type.name)
    setString(3, row.type.version)
    setUserID(4, row.ownerID)
    setBoolean(5, row.isDeleted)
    setString(6, row.origin)
    setObject(7, row.created)
    setObject(8, row.inserted)
  }
