package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
UPDATE
  ${schema}.dataset_install_message
SET
  status = ?
, message = ?
, updated = ?
WHERE
  dataset_id = ?
  AND install_type = ?
"""

internal fun Connection.updateDatasetInstallMessage(schema: String, message: DatasetInstallMessage) {
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, message.status.value)
      ps.setString(2, message.message)
      ps.setObject(3, message.updated)
      ps.setString(4, message.datasetID.toString())
      ps.setString(5, message.installType.value)
      ps.executeUpdate()
    }
}