package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.InstallType
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
  preparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setInstallType(2, installType)
  }
