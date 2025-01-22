package vdi.component.db.cache.sql.insert

import vdi.component.db.cache.model.Dataset
import vdi.component.db.cache.util.preparedUpdate
import vdi.component.db.cache.util.setDataType
import vdi.component.db.cache.util.setDatasetID
import vdi.component.db.cache.util.setUserID
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.datasets (
    dataset_id
  , user_stable_id
  , type_name
  , type_version
  , owner_id
  , is_deleted
  , origin
  , created
  , inserted
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id)
  DO NOTHING
"""

internal fun Connection.tryInsertDatasetRecord(row: Dataset) =
  preparedUpdate(SQL) {
    setDatasetID(1, row.datasetID)
    setString(2, row.userStableID)
    setDataType(3, row.typeName)
    setString(4, row.typeVersion)
    setUserID(5, row.ownerID)
    setBoolean(6, row.isDeleted)
    setString(7, row.origin)
    setObject(8, row.created)
    setObject(9, row.inserted)
  }
