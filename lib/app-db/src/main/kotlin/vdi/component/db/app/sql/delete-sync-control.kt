package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.sync_control
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteSyncControl(schema: String, datasetID: DatasetID): Int =
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.executeUpdate()
    }