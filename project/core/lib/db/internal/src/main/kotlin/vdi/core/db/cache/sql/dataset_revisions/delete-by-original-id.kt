package vdi.core.db.cache.sql.dataset_revisions

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.meta.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_revisions
WHERE
  original_id = ?
"""

/**
 * Deletes dataset revision records with the given original/root dataset ID.
 *
 * @return The number of records deleted.
 */
internal fun Connection.deleteDatasetRevisions(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) }
