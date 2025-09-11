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
// language=postgresql
"""
UPDATE
  ${schema}.${Table.InstallMessage}
SET
  status = ?
, message = ?
, updated = ?
WHERE
  dataset_id = ?
  AND install_type = ?
"""

internal fun Connection.updateDatasetInstallMessage(schema: String, message: DatasetInstallMessage) {
  withPreparedUpdate(sql(schema)) {
    setInstallStatus(1, message.status)
    setString(2, message.message)
    setDateTime(3, message.updated)
    setDatasetID(4, message.datasetID)
    setInstallType(5, message.installType)
  }
}
