package vdi.lib.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import vdi.lib.db.app.model.InstallType
import vdi.lib.db.app.sql.setInstallType
import vdi.lib.db.jdbc.setDatasetID

private fun bulkSQL(schema: String) =
// language=oracle
  """
DELETE FROM
  ${schema}.dataset_install_message
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteInstallMessages(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(bulkSQL(schema)) { setDatasetID(1, datasetID) }

//

private fun singleSQL(schema: String) =
// language=oracle
  """
DELETE FROM
  ${schema}.dataset_install_message
WHERE
  dataset_id = ?
  AND install_type = ?
"""

internal fun Connection.deleteInstallMessage(schema: String, datasetID: DatasetID, installType: InstallType) =
  withPreparedUpdate(singleSQL(schema)) {
    setDatasetID(1, datasetID)
    setInstallType(2, installType)
  }
