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
  install_type
, status
, message
, updated
FROM
  ${schema}.dataset_install_message
WHERE
  dataset_id = ?
"""

internal fun Connection.selectDatasetInstallMessages(
  schema: String,
  datasetID: DatasetID
): List<DatasetInstallMessage> {
  prepareStatement(sql(schema)).use { ps ->
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
          message     = rs.getString("message"),
          updated     = rs.getObject("updated", OffsetDateTime::class.java)
        ))
      } while (rs.next())

      return out
    }
  }
}