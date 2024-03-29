package vdi.component.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.util.preparedUpdate
import vdi.component.db.cache.util.setDatasetID
import vdi.component.db.cache.util.setUserID
import java.sql.Connection
import java.time.OffsetDateTime

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

internal fun Connection.tryInsertDatasetRecord(
  datasetID:   DatasetID,
  typeName:    String,
  typeVersion: String,
  ownerID:     UserID,
  isDeleted:   Boolean,
  origin:      String,
  created:     OffsetDateTime,
  inserted:    OffsetDateTime,
) =
  preparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setString(2, typeName.lowercase())
    setString(3, typeVersion)
    setUserID(4, ownerID)
    setBoolean(5, isDeleted)
    setString(6, origin)
    setObject(7, created)
    setObject(8, inserted)
  }
