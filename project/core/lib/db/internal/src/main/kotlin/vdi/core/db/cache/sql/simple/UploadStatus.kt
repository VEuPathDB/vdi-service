package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withPreparedUpdate
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.DatasetUploadStatus
import vdi.model.meta.DatasetID

internal object UploadStatus {
  // language=postgresql
  private const val UpsertSQL = """
INSERT INTO
  vdi.upload_status (dataset_id, status)
VALUES
  (?, ?)
ON CONFLICT (dataset_id)
DO UPDATE SET
  status = ?
"""

  context(con: Connection)
  fun upsert(datasetID: DatasetID, status: DatasetUploadStatus) {
    con.withPreparedUpdate(UpsertSQL) {
      setDatasetID(1, datasetID)
      status.toString().also {
        setString(2, it) // INSERT status value
        setString(3, it) // UPDATE status value
      }
    }
  }

  // language=postgresql
  private const val SelectSingleSQL = """
SELECT
  status
FROM
  vdi.upload_status
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  fun select(datasetID: DatasetID): DatasetUploadStatus? =
    con.withPreparedStatement(SelectSingleSQL) {
      setDatasetID(1, datasetID)
      withResults {
        if (next())
          DatasetUploadStatus.fromString(getString(1))
        else
          null
      }
    }
}