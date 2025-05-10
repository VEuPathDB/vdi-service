package vdi.lib.db.app.sql.dataset_publication

import io.foxcapades.kdbc.withPreparedBatchUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication
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
  publications: Iterable<VDIDatasetPublication>,
) {
  withPreparedBatchUpdate(sql(schema), publications) {
    setDatasetID(1, datasetID)
    setString(2, it.pubmedID)
    setString(3, it.citation)
  }
}
