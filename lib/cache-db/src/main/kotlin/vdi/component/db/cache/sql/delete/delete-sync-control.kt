package vdi.component.db.cache.sql.delete

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.util.preparedUpdate
import vdi.component.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.sync_control
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteSyncControl(datasetID: DatasetID) =
  preparedUpdate(SQL) { setDatasetID(1, datasetID) }
