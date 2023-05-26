package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import java.sql.Connection

// language=oracle
private const val SQL = """
INSERT INTO
  vdi.dataset_install_message (
    dataset_id
  , install_type
  , status
  , message
  )
VALUES
  (?, ?, ?, ?)
"""

internal fun Connection.insertDatasetInstallMessage(message: DatasetInstallMessage) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, message.datasetID.toString())
      ps.setString(2, message.installType.value)
      ps.setString(3, message.status.value)
      ps.setString(4, message.message)
      ps.executeUpdate()
    }
}