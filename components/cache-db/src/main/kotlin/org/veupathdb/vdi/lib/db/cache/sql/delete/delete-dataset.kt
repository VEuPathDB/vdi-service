package org.veupathdb.vdi.lib.db.cache.sql.delete

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.datasets
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDataset(datasetID: DatasetID) {
  prepareStatement(SQL).use { ps ->
    ps.setDatasetID(1, datasetID)
    ps.execute()
  }
}