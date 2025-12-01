package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.*
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetFileInfo
import vdi.model.meta.DatasetID

internal object DatasetInstallFiles {

  // language=postgresql
  private const val DeleteSQL = """
DELETE FROM
  vdi.install_files
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun delete(datasetID: DatasetID) =
    con.withPreparedUpdate(DeleteSQL) { setDatasetID(1, datasetID) }


  // language=postgresql
  private const val InsertSQL = """
INSERT INTO
  vdi.install_files (dataset_id, file_name, file_size)
VALUES
  (?, ?, ?)
ON CONFLICT
  DO NOTHING
"""

  context(con: Connection)
  internal fun tryInsert(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) =
    con.withPreparedBatchUpdate(InsertSQL, files) {
      setDatasetID(1, datasetID)
      setString(2, it.name)
      setLong(3, it.size.toLong())
    }
      .reduceOrNull { a, b -> a + b } ?: 0

  // language=postgresql
  private const val CountSQL = """
SELECT
  count(1)
FROM
  vdi.install_files
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun count(datasetID: DatasetID) =
    con.withPreparedStatement(CountSQL) {
      setDatasetID(1, datasetID)
      withResults { next(); getInt(1) }
    }

  // language=postgresql
  private const val SelectSQL = """
SELECT
  file_name
, file_size
FROM
  vdi.install_files
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun select(datasetID: DatasetID): List<DatasetFileInfo> =
    con.withPreparedStatement(SelectSQL) {
      setDatasetID(1, datasetID)
      withResults { map { DatasetFileInfo(it.getString(1), it.getLong(2).toULong()) } }
    }
}