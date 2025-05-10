package vdi.lib.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.forEach
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import vdi.lib.db.app.model.InstallStatuses
import vdi.lib.db.app.model.InstallType
import vdi.lib.db.app.sql.getInstallStatus
import vdi.lib.db.app.sql.getInstallType
import vdi.lib.db.jdbc.setDatasetID

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
