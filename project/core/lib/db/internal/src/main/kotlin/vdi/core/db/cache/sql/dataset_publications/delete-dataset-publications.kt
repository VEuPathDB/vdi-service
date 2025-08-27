package vdi.core.db.cache.sql.dataset_publications

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_publications
WHERE
  dataset_id = ?
"""

/**
 * Deletes all the publication records associated with the given dataset ID.
 */
internal fun Connection.deleteDatasetPublications(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) }