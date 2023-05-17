package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import java.sql.Connection

// language=oracle
private const val SQL = """
UPDATE
  vdi.dataset_install_messages
SET
  status = ?
, message = ?
WHERE
  dataset_id = ?
  AND install_type = ?
"""

internal fun Connection.updateDatasetInstallMessage(message: DatasetInstallMessage) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, message.status.value)
      ps.setString(2, message.message)
      ps.setString(3, message.datasetID.toString())
      ps.setString(4, message.installType.value)
      ps.executeUpdate()
    }
}