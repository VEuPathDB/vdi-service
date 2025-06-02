package vdi.core.db.app.sql.dataset_visibility

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.app.model.DatasetVisibilityRecord
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.setDatasetID

private fun sql(schema: String) =
// language=oracle
"""
SELECT
  user_id
FROM
  ${schema}.dataset_visibility
WHERE
  dataset_id = ?
"""

internal fun Connection.selectDatasetVisibilityRecords(
  schema: String,
  datasetID: DatasetID
): List<DatasetVisibilityRecord> =
  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)
    withResults { map { DatasetVisibilityRecord(datasetID, getUserID("user_id")) } }
  }
