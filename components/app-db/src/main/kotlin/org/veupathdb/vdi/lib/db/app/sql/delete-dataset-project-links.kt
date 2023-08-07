package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_project
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetProjectLinks(schema: String, datasetID: DatasetID): Int =
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.executeUpdate()
    }