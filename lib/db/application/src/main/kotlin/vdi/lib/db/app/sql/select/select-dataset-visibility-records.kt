package vdi.lib.db.app.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.lib.db.app.model.DatasetVisibilityRecord
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

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
