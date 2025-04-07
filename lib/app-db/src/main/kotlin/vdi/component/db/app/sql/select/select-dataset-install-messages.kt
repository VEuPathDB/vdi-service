package vdi.component.db.app.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.DatasetInstallMessage
import vdi.component.db.app.sql.getInstallStatus
import vdi.component.db.app.sql.getInstallType
import vdi.component.db.jdbc.getDateTime
import vdi.component.db.jdbc.setDatasetID
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
