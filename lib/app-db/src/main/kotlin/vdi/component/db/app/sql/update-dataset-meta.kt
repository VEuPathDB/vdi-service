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
  preparedUpdate(sql(schema)) {
    setString(1, name)
    // limit to fit into varchar(4000)
    setString(2, summary?.take(MaxSummaryFieldLength))
    setString(3, description)
    setDatasetID(4, datasetID)
  }
}
