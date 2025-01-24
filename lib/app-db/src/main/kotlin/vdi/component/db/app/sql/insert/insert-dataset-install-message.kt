package vdi.component.db.app.sql.insert

import vdi.component.db.app.model.DatasetInstallMessage
import vdi.component.db.app.sql.*
import java.sql.Connection

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
  preparedUpdate(sql(schema)) {
    setDatasetID(1, message.datasetID)
    setInstallType(2, message.installType)
    setInstallStatus(3, message.status)
    setString(4, message.message)
    setDateTime(5, message.updated)
  }
}
