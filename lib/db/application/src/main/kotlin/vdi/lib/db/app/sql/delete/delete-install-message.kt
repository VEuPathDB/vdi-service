package vdi.lib.db.app.sql.delete

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.lib.db.app.model.InstallType
import vdi.lib.db.app.sql.setInstallType
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_install_message
WHERE
  dataset_id = ?
  AND install_type = ?
"""

internal fun Connection.deleteInstallMessage(schema: String, datasetID: DatasetID, installType: InstallType) =
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setInstallType(2, installType)
  }
