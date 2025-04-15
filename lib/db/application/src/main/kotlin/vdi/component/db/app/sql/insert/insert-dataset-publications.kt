package vdi.component.db.app.sql.insert

import io.foxcapades.kdbc.withPreparedBatchUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection


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
