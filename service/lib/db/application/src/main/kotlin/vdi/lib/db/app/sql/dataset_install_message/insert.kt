package vdi.lib.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.lib.db.app.model.DatasetInstallMessage
import vdi.lib.db.app.sql.setInstallStatus
import vdi.lib.db.app.sql.setInstallType
import vdi.lib.db.jdbc.setDatasetID
import vdi.lib.db.jdbc.setDateTime

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
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, message.datasetID)
    setInstallType(2, message.installType)
    setInstallStatus(3, message.status)
    setString(4, message.message)
    setDateTime(5, message.updated)
  }
}
