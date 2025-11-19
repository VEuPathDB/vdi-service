package vdi.core.db.cache.sql.datasets

import io.foxcapades.kdbc.withPreparedStatement
import vdi.model.meta.DatasetID
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
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)

    var count = executeUpdate()
    while (moreResults) {
      count += updateCount
    }

    count
  }
