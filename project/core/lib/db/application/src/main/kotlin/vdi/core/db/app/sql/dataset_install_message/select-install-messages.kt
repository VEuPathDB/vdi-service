package vdi.core.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.getInstallStatus
import vdi.core.db.app.sql.getInstallType
import vdi.core.db.jdbc.getDateTime
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID

private fun sql(schema: String) =
// language=sql
"""
SELECT
  install_type
, status
, message
, updated
FROM
  ${schema}.${Table.InstallMessage}
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
