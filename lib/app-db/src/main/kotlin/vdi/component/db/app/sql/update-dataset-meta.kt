package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.MaxSummaryFieldLength
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
UPDATE
  ${schema}.dataset_meta
SET
  name = ?
, summary = ?
, description = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetMeta(schema: String, datasetID: DatasetID, name: String, summary: String?, description: String?) {
  // limit to fit into varchar(4000)
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, name)
      ps.setString(2, summary.take(MaxSummaryFieldLength))
      ps.setString(3, description)
      ps.setString(4, datasetID.toString())
      ps.executeUpdate()
    }
}