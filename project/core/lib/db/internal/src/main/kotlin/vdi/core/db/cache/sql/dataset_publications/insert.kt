package vdi.core.db.cache.sql.dataset_publications

import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID
import vdi.model.data.DatasetPublication

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_publications (dataset_id, publication_type, publication_id)
VALUES
  (?, ?, ?)
"""

internal fun Connection.insertDatasetPublications(datasetID: DatasetID, publications: Iterable<DatasetPublication>) =
  withPreparedBatchUpdate(SQL, publications) {
    setDatasetID(1, datasetID)
    setInt(2, it.type.ordinal)
    setString(3, it.identifier)
  }.reduceOrNull(Int::plus) ?: 0
