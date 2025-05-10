package vdi.lib.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import vdi.lib.db.app.model.DatasetInstallMessage
import vdi.lib.db.app.model.InstallStatus
import vdi.lib.db.app.model.InstallType
import vdi.lib.db.app.sql.setInstallType
import vdi.lib.db.jdbc.setDatasetID

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
) =
  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)
    setInstallType(2, installType)

    withResults {
      if (!next())
        null
      else
        DatasetInstallMessage(
          datasetID   = datasetID,
          installType = installType,
          status      = InstallStatus.fromString(get("status")),
          message     = get("message"),
          updated     = get("updated"),
        )
    }
  }
