package vdi.core.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.forEach
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.sql.getInstallStatus
import vdi.core.db.app.sql.getInstallType
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID

private fun sql(schema: String) =
// language=postgresql
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

  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)
    withResults { forEach {
      val type    = getInstallType("install_type")
      val status  = getInstallStatus("status")
      val message = getString("message")

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
    } }
  }

  return result
}
