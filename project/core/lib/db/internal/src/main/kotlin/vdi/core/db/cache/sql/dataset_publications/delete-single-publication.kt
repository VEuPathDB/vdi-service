package vdi.core.db.cache.sql.dataset_publications

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_publications
WHERE
  dataset_id = ?
  AND publication_id = ?
"""

internal fun Connection.deleteDatasetPublication(datasetID: DatasetID, publicationID: String) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setString(2, publicationID)
  }