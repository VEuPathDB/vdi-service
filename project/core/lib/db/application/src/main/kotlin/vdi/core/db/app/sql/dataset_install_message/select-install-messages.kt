package vdi.core.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.sql.getInstallStatus
import vdi.core.db.app.sql.getInstallType
import vdi.lib.db.jdbc.getDateTime
import vdi.lib.db.jdbc.setDatasetID

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
): List<DatasetInstallMessage> =
  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)

    withResults {
      if (!next())
        return emptyList()
      else
        ArrayList<DatasetInstallMessage>(2).also {
          do {
            it.add(DatasetInstallMessage(
              datasetID   = datasetID,
              installType = getInstallType("install_type"),
              status      = getInstallStatus("status"),
              message     = getString("message"),
              updated     = getDateTime("updated")
            ))
          } while (next())
        }
    }
  }
