package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.InstallStatus
import vdi.component.db.app.model.InstallStatuses
import vdi.component.db.app.model.InstallType
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
SELECT
  dataset_id
, install_type
, status
, message
FROM
  ${schema}.dataset_install_message
WHERE
  dataset_id = ?
"""

internal fun Connection.selectInstallStatuses(schema: String, datasetID: DatasetID): InstallStatuses {
  val result = InstallStatuses()

  prepareStatement(sql(schema)).use { ps ->
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
