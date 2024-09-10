package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
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
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, name)
      ps.setString(2, summary)
      ps.setString(3, description)
      ps.setString(4, datasetID.toString())
      ps.executeUpdate()
    }
}