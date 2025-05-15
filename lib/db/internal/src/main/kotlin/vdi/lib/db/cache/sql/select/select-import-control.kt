package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import vdi.lib.db.cache.util.getImportStatus
import vdi.lib.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
SELECT
  status
FROM
  vdi.import_control
WHERE
    dataset_id = ?
"""

internal fun Connection.selectImportControl(datasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)

    withResults {
      if (!next())
        null
      else
        getImportStatus(1)!!
    }
  }
