package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.DatasetInstallMessage
import vdi.component.db.app.model.InstallStatus
import vdi.component.db.app.model.InstallType
import java.sql.Connection
import java.time.OffsetDateTime

private fun sql(schema: String) =
// language=oracle
"""
SELECT
  status
, message
, updated
FROM
  ${schema}.dataset_install_message
WHERE
  dataset_id = ?
  AND install_type = ?
"""

internal fun Connection.selectDatasetInstallMessage(
  schema: String,
  datasetID: DatasetID,
  installType: InstallType
): DatasetInstallMessage? {
  prepareStatement(sql(schema)).use { ps ->
    ps.setDatasetID(1, datasetID)
    ps.setInstallType(2, installType)

    ps.executeQuery().use { rs ->
      if (!rs.next())
        return null

      return DatasetInstallMessage(
        datasetID   = datasetID,
        installType = installType,
        status      = InstallStatus.fromString(rs.getString("status")),
        message     = rs.getString("message"),
        updated     = rs.getObject("updated", OffsetDateTime::class.java),
      )
    }
  }
}
