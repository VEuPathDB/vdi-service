package org.veupathdb.vdi.lib.db.cache.sql.delete

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.util.preparedUpdate
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_files
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetFiles(datasetID: DatasetID) =
  preparedUpdate(SQL) { setDatasetID(1, datasetID) }
