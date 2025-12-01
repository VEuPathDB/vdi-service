package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.*
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetPublication

internal object DatasetPublicationsTable {

  // language=postgresql
  private const val DeleteAllSQL = """
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
  context(con: Connection)
  internal fun delete(datasetID: DatasetID) =
    con.withPreparedUpdate(DeleteAllSQL) { setDatasetID(1, datasetID) }


  // language=postgresql
  private const val DeleteSingleSQL = """
DELETE FROM
  vdi.dataset_publications
WHERE
  dataset_id = ?
  AND publication_id = ?
"""

  context(con: Connection)
  internal fun delete(datasetID: DatasetID, publicationID: String) =
    con.withPreparedUpdate(DeleteSingleSQL) {
      setDatasetID(1, datasetID)
      setString(2, publicationID)
    }

  // language=postgresql
  private const val InsertSQL = """
INSERT INTO
  vdi.dataset_publications (dataset_id, publication_type, publication_id)
VALUES
  (?, ?, ?)
ON CONFLICT DO NOTHING
"""

  context(con: Connection)
  internal fun tryInsert(datasetID: DatasetID, publications: Iterable<DatasetPublication>) =
    con.withPreparedBatchUpdate(InsertSQL, publications) {
      setDatasetID(1, datasetID)
      setInt(2, it.type.ordinal)
      setString(3, it.identifier)
    }.reduceOrNull(Int::plus) ?: 0


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
  context(con: Connection)
  internal fun select(datasetID: DatasetID) =
    con.withPreparedStatement(SQL) {
      setDatasetID(1, datasetID)

      usingResults { rs -> rs.map {
        DatasetPublication(
          identifier = it["publication_id"],
          type = DatasetPublication.PublicationType.entries[it["publication_type"]]
        )
      } }
    }}