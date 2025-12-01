package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withPreparedUpdate
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.cache.model.Dataset
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setDataType
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID

internal object DatasetsTable {

  // language=postgresql
  private const val InsertSQL = """
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

  context(con: Connection)
  internal fun tryInsert(row: Dataset) =
    con.withPreparedUpdate(InsertSQL) {
      setDatasetID(1, row.datasetID)
      setDataType(2, row.type.name)
      setString(3, row.type.version)
      setUserID(4, row.ownerID)
      setBoolean(5, row.isDeleted)
      setString(6, row.origin)
      setObject(7, row.created)
      setObject(8, row.inserted)
    }

  // language=postgresql
  private const val UpdateDeletedSQL = """
UPDATE
  vdi.datasets
SET
  is_deleted = ?
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun update(datasetID: DatasetID, deleted: Boolean) =
    con.withPreparedUpdate(UpdateDeletedSQL) {
      setBoolean(1, deleted)
      setDatasetID(2, datasetID)
    }

  // language=postgresql
  private const val SelectIDsSQL = """
SELECT
  d.dataset_id
FROM
  vdi.datasets AS d
WHERE
  d.owner_id = ?
  AND d.is_deleted = FALSE
"""

  context(con: Connection)
  internal fun selectIDs(userID: UserID): List<DatasetID> =
    con.withPreparedStatement(SelectIDsSQL) {
      setUserID(1, userID)
      withResults { map { reqDatasetID("dataset_id") } }
    }
}