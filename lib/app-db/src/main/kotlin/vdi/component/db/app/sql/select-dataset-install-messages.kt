package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.DatasetInstallMessage
import java.sql.Connection

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
    ps.setDatasetID(1, datasetID)

    ps.executeQuery().use { rs ->
      if (!rs.next())
        return emptyList()

      val out = ArrayList<DatasetInstallMessage>(2)

      do {
        out.add(DatasetInstallMessage(
          datasetID   = datasetID,
          installType = rs.getInstallType("install_type"),
          status      = rs.getInstallStatus("status"),
          message     = rs.getString("message"),
          updated     = rs.getDateTime("updated")
        ))
      } while (rs.next())

      return out
    }
  }
}
