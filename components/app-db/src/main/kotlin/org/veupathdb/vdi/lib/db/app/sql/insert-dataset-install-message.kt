package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import java.sql.Connection
import java.time.OffsetDateTime

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.dataset_install_message (
    dataset_id
  , install_type
  , status
  , message
  , updated
  )
VALUES
  (?, ?, ?, ?, ?)
"""

internal fun Connection.insertDatasetInstallMessage(schema: String, message: DatasetInstallMessage) {
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, message.datasetID.toString())
      ps.setString(2, message.installType.value)
      ps.setString(3, message.status.value)
      ps.setString(4, message.message)
      ps.setObject(5, message.updated)
      ps.executeUpdate()
    }
}