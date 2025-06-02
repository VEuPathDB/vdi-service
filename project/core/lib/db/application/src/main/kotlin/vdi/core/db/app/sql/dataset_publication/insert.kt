package vdi.core.db.app.sql.dataset_publication

import io.foxcapades.kdbc.withPreparedBatchUpdate
import vdi.model.data.DatasetID
import vdi.model.data.DatasetPublication
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID


private fun sql(schema: String) =
// language=oracle
  """
INSERT INTO
  ${schema}.dataset_publication (
    dataset_id
  , pubmed_id
  , citation
  )
VALUES
  (?, ?, ?)
"""

internal fun Connection.insertDatasetPublications(
  schema: String,
  datasetID: DatasetID,
  publications: Iterable<DatasetPublication>,
) {
  withPreparedBatchUpdate(sql(schema), publications) {
    setDatasetID(1, datasetID)
    setString(2, it.pubmedID)
    setString(3, it.citation)
  }
}
