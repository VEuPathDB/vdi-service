package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.dataset_meta (
    dataset_id
  , name
  , description
  )
VALUES
  (?, ?, ?)
"""

internal fun Connection.insertDatasetMeta(schema: String, datasetID: DatasetID, name: String, description: String?) {
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, name)
      ps.setString(3, description)
      ps.executeUpdate()
    }
}