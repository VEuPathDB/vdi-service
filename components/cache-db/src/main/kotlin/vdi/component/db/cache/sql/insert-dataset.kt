package vdi.component.db.cache.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
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
  , created
  )
VALUES
  (?, ?, ?, ?, ?, ?)
"""

fun Connection.insertDatasetRecord(
  datasetID: DatasetID,
  typeName: String,
  typeVersion: String,
  ownerID: UserID,
  isDeleted: Boolean,
  created: OffsetDateTime,
) {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.setString(2, typeName)
    ps.setString(3, typeVersion)
    ps.setString(4, ownerID.toString())
    ps.setBoolean(5, isDeleted)
    ps.setObject(6, created)

    ps.execute()
  }
}