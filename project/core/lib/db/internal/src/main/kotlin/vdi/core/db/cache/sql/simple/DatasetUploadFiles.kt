package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.*
import java.sql.Connection
import kotlin.collections.set
import vdi.core.db.cache.model.DatasetFileSummary
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetFileInfo
import vdi.model.meta.DatasetID

internal object DatasetUploadFiles {

  // language=postgresql
  private const val DeleteSQL = """
DELETE FROM
  vdi.upload_files
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
  vdi.upload_files
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
  vdi.upload_files
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun select(datasetID: DatasetID): List<DatasetFileInfo> =
    con.withPreparedStatement(SelectSQL) {
      setDatasetID(1, datasetID)
      withResults { map { DatasetFileInfo(it.getString(1), it.getLong(2).toULong()) } }
    }


  // language=postgresql
  private const val SummarySQL = """
SELECT
  dataset_id
, count(1) AS count
, sum(v.file_size) AS size
FROM
  vdi.upload_files AS v
  INNER JOIN unnest(?::VARCHAR[]) AS dataset_ids(dataset_id)
    USING(dataset_id)
GROUP BY
  dataset_id
"""

  context(con: Connection)
  internal fun selectSummaries(datasetIDs: List<DatasetID>): Map<DatasetID, DatasetFileSummary> =
    con.withPreparedStatement(SummarySQL) {
      val idArray = Array<Any>(datasetIDs.size) { i -> datasetIDs[i].toString() }
      setArray(1, con.createArrayOf("VARCHAR", idArray))
      withResults {
        val out = HashMap<DatasetID, DatasetFileSummary>(datasetIDs.size)

        while (next()) {
          out[reqDatasetID("dataset_id")] = DatasetFileSummary(getInt("count").toUInt(), getLong("size").toULong())
        }

        out
      }
    }
}