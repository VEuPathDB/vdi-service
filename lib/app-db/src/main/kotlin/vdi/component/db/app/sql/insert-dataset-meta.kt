package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.MaxSummaryFieldLength
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
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
"""

internal fun Connection.insertDatasetMeta(schema: String, datasetID: DatasetID, name: String, summary: String?, description: String?) {
  // limit to fit into varchar(4000)
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, name)
      ps.setString(3, summary?.take(MaxSummaryFieldLength))
      ps.setString(4, description)
      ps.executeUpdate()
    }
}