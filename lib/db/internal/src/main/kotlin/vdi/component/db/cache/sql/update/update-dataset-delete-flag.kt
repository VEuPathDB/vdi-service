package vdi.component.db.cache.sql.update

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
UPDATE
  vdi.datasets
SET
  is_deleted = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetDeleteFlag(datasetID: DatasetID, deleted: Boolean) =
  withPreparedUpdate(SQL) {
    setBoolean(1, deleted)
    setDatasetID(2, datasetID)
  }
