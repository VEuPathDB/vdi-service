package vdi.component.db.app.sql.delete

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.sql.preparedUpdate
import vdi.component.db.app.sql.setDatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_install_message
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteInstallMessages(schema: String, datasetID: DatasetID) =
  preparedUpdate(sql(schema)) { setDatasetID(1, datasetID) }
