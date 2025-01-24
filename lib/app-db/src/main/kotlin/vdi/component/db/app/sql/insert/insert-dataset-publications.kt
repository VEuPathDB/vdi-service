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
  , citation
  , pubmed_id
  )
VALUES
  (?, ?, ?)
"""

internal fun Connection.insertDatasetPublications(
  schema: String,
  datasetID: DatasetID,
  publications: Collection<VDIDatasetPublication>,
) {
  preparedBatchUpdate(sql(schema), publications) {
    setDatasetID(1, datasetID)
    setString(2, it.citation)
    setString(3, it.pubmedID)
  }
}
