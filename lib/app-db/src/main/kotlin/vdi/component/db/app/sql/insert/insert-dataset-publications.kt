package vdi.component.db.app.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication
import vdi.component.db.app.sql.preparedBatchUpdate
import vdi.component.db.app.sql.setDatasetID
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
  preparedBatchUpdate(sql(schema), publications) {
    setDatasetID(1, datasetID)
    setString(2, it.pubmedID)
    setString(3, it.citation)
  }
}
