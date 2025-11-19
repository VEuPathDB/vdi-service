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
"""

/**
 * Deletes all the publication records associated with the given dataset ID.
 *
 * @param datasetID ID of the dataset for which all publication records should
 * be deleted.
 *
 * @return The number of records deleted as a result of this function call.
 */
internal fun Connection.deleteAllPublicationsForDataset(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) }