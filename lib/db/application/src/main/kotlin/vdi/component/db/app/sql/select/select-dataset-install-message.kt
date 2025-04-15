package vdi.component.db.app.sql.select

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.DatasetInstallMessage
import vdi.component.db.app.model.InstallStatus
import vdi.component.db.app.model.InstallType
import vdi.component.db.app.sql.setInstallType
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

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
