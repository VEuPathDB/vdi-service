package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withPreparedUpdate
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.util.getImportStatus
import vdi.core.db.cache.util.setImportStatus
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID

internal object ImportControlTable {

  // language=postgresql
  private const val DeleteSQL = """
DELETE FROM
  vdi.import_control
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun delete(datasetID: DatasetID) =
    con.withPreparedUpdate(DeleteSQL) { setDatasetID(1, datasetID) }


  // language=postgresql
  private const val InsertSQL = """
INSERT INTO
  vdi.import_control (
    dataset_id
  , status
  )
VALUES
  (?, ?)
ON CONFLICT (dataset_id)
  DO NOTHING
"""

  context(con: Connection)
  internal fun tryInsert(datasetID: DatasetID, status: DatasetImportStatus) =
    con.withPreparedUpdate(InsertSQL) {
      setDatasetID(1, datasetID)
      setImportStatus(2, status)
    }


  // language=postgresql
  private const val SelectSQL = """
SELECT
  status
FROM
  vdi.import_control
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun select(datasetID: DatasetID) =
    con.withPreparedStatement(SelectSQL) {
      setDatasetID(1, datasetID)

      withResults {
        if (!next())
          null
        else
          getImportStatus(1)!!
      }
    }

  // language=postgresql
  private const val UpdateSQL = """
UPDATE
  vdi.import_control
SET
  status = ?
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun update(datasetID: DatasetID, syncStatus: DatasetImportStatus) =
    con.withPreparedUpdate(UpdateSQL) {
      setImportStatus(1, syncStatus)
      setDatasetID(2, datasetID)
    }

  // language=postgresql
  private const val UpsertSQL = """
INSERT INTO
  vdi.import_control (dataset_id, status)
VALUES
  (?, ?)
ON CONFLICT (dataset_id) DO UPDATE
SET
  status = ?
"""

  context(con: Connection)
  internal fun upsert(datasetID: DatasetID, status: DatasetImportStatus) =
    con.withPreparedUpdate(UpsertSQL) {
      setDatasetID(1, datasetID)
      setImportStatus(2, status)
      setImportStatus(3, status)
    }

}