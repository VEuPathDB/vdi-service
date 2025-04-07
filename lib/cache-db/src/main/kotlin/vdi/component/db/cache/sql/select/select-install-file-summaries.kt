package vdi.component.db.cache.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.model.DatasetFileSummary
import vdi.component.db.jdbc.getDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  dataset_id
, count(1) AS count
, sum(file_size) AS size
FROM
  vdi.install_files AS v
  INNER JOIN unnest(?::VARCHAR[]) AS dataset_ids(dataset_id)
    USING(dataset_id)
GROUP BY
  dataset_id
"""

internal fun Connection.selectInstallFileSummaries(datasetIDs: List<DatasetID>): Map<DatasetID, DatasetFileSummary> =
  withPreparedStatement(SQL) {
    val idArray = Array<Any>(datasetIDs.size) { i -> datasetIDs[i].toString() }
    setArray(1, createArrayOf("VARCHAR", idArray))
    withResults {
      val out = HashMap<DatasetID, DatasetFileSummary>(datasetIDs.size)

      while (next()) {
        out[getDatasetID("dataset_id")] = DatasetFileSummary(getInt("count").toUInt(), getLong("size").toULong())
      }

      out
    }
  }
