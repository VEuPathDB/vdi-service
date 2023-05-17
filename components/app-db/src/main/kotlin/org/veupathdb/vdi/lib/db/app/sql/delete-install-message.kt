package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.app.model.InstallType
import java.sql.Connection

// language=oracle
private const val SQL = """
DELETE FROM
  vdi.dataset_install_messages
WHERE
  dataset_id = ?
  AND install_type = ?
"""

internal fun Connection.deleteInstallMessage(datasetID: DatasetID, installType: InstallType): Int =
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, installType.value)
      ps.executeUpdate()
    }
