package vdi.core.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.setInstallType
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID

private fun sql(schema: String) =
// language=sql
"""
SELECT
  status
, message
, updated
FROM
  ${schema}.${Table.InstallMessage}
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
