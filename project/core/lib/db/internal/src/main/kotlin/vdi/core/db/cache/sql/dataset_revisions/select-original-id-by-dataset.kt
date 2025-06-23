package vdi.core.db.cache.sql.dataset_revisions

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
SELECT
  original_id
FROM
  vdi.dataset_revisions
WHERE
  revision_id = ?
"""

fun Connection.selectOriginalDatasetID(datasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)

    withResults {
      if (next())
        reqDatasetID(1)
      else
        datasetID
    }
  }
