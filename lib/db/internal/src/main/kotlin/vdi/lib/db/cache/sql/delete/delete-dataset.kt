package vdi.lib.db.cache.sql.delete

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.datasets
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDataset(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) } > 0
