package vdi.component.db.app.sql.update

import vdi.component.db.app.model.DatasetInstallMessage
import vdi.component.db.app.sql.*
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
  preparedUpdate(sql(schema)) {
    setInstallStatus(1, message.status)
    setString(2, message.message)
    setDateTime(3, message.updated)
    setDatasetID(4, message.datasetID)
    setInstallType(5, message.installType)
  }
}
