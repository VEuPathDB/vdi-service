package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import org.veupathdb.vdi.lib.db.app.model.InstallStatus
import org.veupathdb.vdi.lib.db.app.model.InstallType
import java.sql.Connection

// language=oracle
private const val SQL = """
SELECT
  status
, message
FROM
  vdi.dataset_install_message
WHERE
  dataset_id = ?
  AND install_type = ?
"""

internal fun Connection.selectDatasetInstallMessage(datasetID: DatasetID, installType: InstallType): DatasetInstallMessage? {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.setString(2, installType.value)

    ps.executeQuery().use { rs ->
      if (!rs.next())
        return null

      return DatasetInstallMessage(
        datasetID   = datasetID,
        installType = installType,
        status      = InstallStatus.fromString(rs.getString("status")),
        message     = rs.getString("message")
      )
    }
  }
}