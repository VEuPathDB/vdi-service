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
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, name)
      ps.setString(3, summary.take(MaxSummaryFieldLength))
      ps.setString(4, description)
      ps.setString(5, name)
      ps.setString(6, summary.take(MaxSummaryFieldLength))
      ps.setString(7, description)
      ps.executeUpdate()
    }
}