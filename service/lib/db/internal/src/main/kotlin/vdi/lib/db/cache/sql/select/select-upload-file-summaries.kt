package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import vdi.lib.db.cache.model.DatasetFileSummary
import vdi.lib.db.jdbc.reqDatasetID

// language=postgresql
private const val SQL = """
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

internal fun Connection.selectUploadFileSummaries(datasetIDs: List<DatasetID>): Map<DatasetID, DatasetFileSummary> =
  withPreparedStatement(SQL) {
    val idArray = Array<Any>(datasetIDs.size) { i -> datasetIDs[i].toString() }
    setArray(1, createArrayOf("VARCHAR", idArray))
    withResults {
      val out = HashMap<DatasetID, DatasetFileSummary>(datasetIDs.size)

      while (next()) {
        out[reqDatasetID("dataset_id")] = DatasetFileSummary(getInt("count").toUInt(), getLong("size").toULong())
      }

      out
    }
  }
