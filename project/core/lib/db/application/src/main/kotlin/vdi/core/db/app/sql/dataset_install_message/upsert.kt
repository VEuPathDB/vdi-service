package vdi.core.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.sql.Table

private fun sql(schema: String) =
// language=postgresql
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
ON CONFLICT (dataset_id, install_type) DO UPDATE SET
  status=?,
  message=?,
  updated=?
"""

internal fun Connection.upsertDatasetInstallMessage(schema: String, message: DatasetInstallMessage) {
  withPreparedUpdate(sql(schema)) {
    setString(1, message.datasetID.toString())
    setString(2, message.installType.value)
    setString(3, message.status.value)
    setString(4, message.message)
    setObject(5, message.updated)

    setString(6, message.status.value)
    setString(7, message.message)
    setObject(8, message.updated)
  }
}
