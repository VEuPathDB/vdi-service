package vdi.core.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.setInstallStatus
import vdi.core.db.app.sql.setInstallType
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setDateTime

private fun sql(schema: String) =
// language=sql
"""
INSERT INTO
  ${schema}.${Table.InstallMessage} (
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
