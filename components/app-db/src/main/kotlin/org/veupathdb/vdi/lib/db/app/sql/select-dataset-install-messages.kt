package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import org.veupathdb.vdi.lib.db.app.model.InstallStatus
import org.veupathdb.vdi.lib.db.app.model.InstallType
import java.sql.Connection

// language=oracle
private const val SQL = """
SELECT
  install_type
, status
, message
FROM
  vdi.dataset_install_message
WHERE
  dataset_id = ?
"""

internal fun Connection.selectDatasetInstallMessages(datasetID: DatasetID): List<DatasetInstallMessage> {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())

    ps.executeQuery().use { rs ->
      if (!rs.next())
        return emptyList()

      val out = ArrayList<DatasetInstallMessage>(2)

      do {
        out.add(DatasetInstallMessage(
          datasetID   = datasetID,
          installType = InstallType.fromString(rs.getString("install_type")),
          status      = InstallStatus.fromString(rs.getString("status")),
          message     = rs.getString("message")
        ))
      } while (rs.next())

      return out
    }
  }
}