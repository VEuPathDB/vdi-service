package vdi.core.db.cache.sql.datasets

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.datasets
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDataset(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) } > 0
