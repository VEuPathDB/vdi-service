package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import vdi.lib.db.jdbc.reqDatasetID
import vdi.lib.db.jdbc.setDatasetID

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
