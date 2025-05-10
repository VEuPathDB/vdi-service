package vdi.component.db.app.sql.delete

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.InstallType
import vdi.component.db.app.sql.preparedUpdate
import vdi.component.db.app.sql.setDatasetID
import vdi.component.db.app.sql.setInstallType
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
