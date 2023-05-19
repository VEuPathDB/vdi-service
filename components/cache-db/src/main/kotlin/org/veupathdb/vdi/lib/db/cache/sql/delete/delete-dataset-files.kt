package org.veupathdb.vdi.lib.db.cache.sql.delete

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_files
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetFiles(datasetID: DatasetID) {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.execute()
  }
}