package vdi.component.db.app.sql.upsert

import vdi.component.db.app.model.DatasetInstallMessage
import vdi.component.db.app.sql.preparedUpdate
import java.sql.Connection

private fun sql(schema: String) =
// language=psql
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
ON CONFLICT (dataset_id, install_type) DO UPDATE SET
  status=?,
  message=?,
  updated=?
"""

internal fun Connection.upsertDatasetInstallMessage(schema: String, message: DatasetInstallMessage) {
  preparedUpdate(sql(schema)) {
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
