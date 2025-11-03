package vdi.core.db.cache.sql.dataset_publications

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.usingResults
import io.foxcapades.kdbc.withPreparedStatement
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID
import vdi.model.data.DatasetPublication

// language=postgresql
private const val SQL = """
SELECT
  publication_type
, publication_id
FROM
  vdi.dataset_publications
WHERE
  dataset_id = ?
"""

/**
 * Selects and returns a list of partial dataset publication records associated
 * with the given [datasetID] value.
 *
 * These records do not have `citation` or `isPrimary` values assigned as these
 * are not tracked in the cache database.  If those values are needed, they must
 * be retrieved from the dataset metadata file in the object store.
 *
 * @param datasetID ID of the dataset whose publication records should be
 * fetched.
 *
 * @return A read-only list of zero or more dataset publication records.
 */
internal fun Connection.selectPublicationsForDataset(datasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)

    usingResults { rs -> rs.map {
      DatasetPublication(
        identifier = it["publication_id"],
        type = DatasetPublication.PublicationType.entries[it["publication_type"]]
      )
    } }
  }