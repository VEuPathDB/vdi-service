package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.db.app.model.InstallStatus
import org.veupathdb.vdi.lib.db.app.model.InstallStatuses
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.app.model.InstallType
import java.sql.Connection

// language=oracle
private const val SQL = """
SELECT
  dataset_id
, install_type
, status
, message
FROM
  vdi.dataset_install_message
WHERE
  dataset_id = ?
"""

internal fun Connection.selectInstallStatuses(datasetID: DatasetID): InstallStatuses {
  val result = InstallStatuses()

  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.executeQuery().use { rs ->
      while (rs.next()) {
        val type    = InstallType.fromString(rs.getString("install_type"))
        val status  = InstallStatus.fromString(rs.getString("status"))
        val message = rs.getString("message")

        when (type) {
          InstallType.Meta -> {
            result.meta = status
            result.metaMessage = message
          }

          InstallType.Data -> {
            result.data = status
            result.dataMessage = message
          }
        }
      }
    }
  }

  return result
}
