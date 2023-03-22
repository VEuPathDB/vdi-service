package org.veupathdb.service.vdi.db.appdb

import org.veupathdb.service.vdi.model.InstallStatus
import org.veupathdb.service.vdi.model.InstallStatuses
import org.veupathdb.service.vdi.model.InstallType
import java.sql.Connection
import vdi.components.common.fields.DatasetID

// language=oracle
private const val SQL = """
SELECT
  dataset_id
, install_type
, status
, message
FROM
  vdi.dataset_install_messages
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
