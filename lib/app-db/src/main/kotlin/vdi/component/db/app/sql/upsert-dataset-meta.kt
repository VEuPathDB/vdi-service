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
  , description
  )
VALUES
  (?, ?, ?)
ON CONFLICT (dataset_id) DO UPDATE SET
  name=?,
  description=?
"""

internal fun Connection.upsertDatasetMeta(schema: String, datasetID: DatasetID, name: String, description: String?) {
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, name)
      ps.setString(3, description)
      ps.setString(4, name)
      ps.setString(5, description)
      ps.executeUpdate()
    }
}