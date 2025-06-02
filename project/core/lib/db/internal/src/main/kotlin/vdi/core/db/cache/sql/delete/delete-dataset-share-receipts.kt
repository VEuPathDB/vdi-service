package vdi.core.db.cache.sql.delete

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_share_receipts
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetShareReceipts(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) }
