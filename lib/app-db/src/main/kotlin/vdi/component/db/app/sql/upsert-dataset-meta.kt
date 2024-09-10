package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
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
  // limit to fit into varchar(4000)
  val summTrunc = if (summary != null && summary.length > 4000) summary.substring(0, 4000) else summary
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, name)
      ps.setString(3, summTrunc)
      ps.setString(4, description)
      ps.setString(5, name)
      ps.setString(6, summTrunc)
      ps.setString(7, description)
      ps.executeUpdate()
    }
}