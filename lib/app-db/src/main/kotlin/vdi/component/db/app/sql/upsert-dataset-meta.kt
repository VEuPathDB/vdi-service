package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.MaxSummaryFieldLength
import java.sql.Connection

private fun sql(schema: String) =
// language=psql
"""
INSERT INTO
  ${schema}.dataset_meta (
    dataset_id
  , name
  , summary
  , description
  )
VALUES
  (?, ?, ?, ?)
ON CONFLICT (dataset_id) DO UPDATE SET
  name=?,
  summary=?,
  description=?
"""

internal fun Connection.upsertDatasetMeta(schema: String, datasetID: DatasetID, name: String, summary: String?, description: String?) {
  preparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, name)
    setString(3, summary?.take(MaxSummaryFieldLength))
    setString(4, description)
    setString(5, name)
    setString(6, summary?.take(MaxSummaryFieldLength))
    setString(7, description)
  }
}
