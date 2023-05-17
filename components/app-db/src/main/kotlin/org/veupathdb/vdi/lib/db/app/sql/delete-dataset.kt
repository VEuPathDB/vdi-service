package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

// language=oracle
private const val SQL = """
DELETE FROM
  vdi.datasets
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDataset(datasetID: DatasetID): Int =
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.executeUpdate()
    }