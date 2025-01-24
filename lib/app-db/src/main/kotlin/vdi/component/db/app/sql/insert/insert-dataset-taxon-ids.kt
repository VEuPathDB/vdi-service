package vdi.component.db.app.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.sql.preparedBatchUpdate
import vdi.component.db.app.sql.setDatasetID
import java.sql.Connection


private fun sql(schema: String) =
// language=oracle
  """
INSERT INTO
  ${schema}.dataset_hyperlink (
    dataset_id
  , taxon_id
  )
VALUES
  (?, ?)
"""

internal fun Connection.insertDatasetTaxonIDs(
  schema: String,
  datasetID: DatasetID,
  taxonIDs: Collection<Long>,
) {
  preparedBatchUpdate(sql(schema), taxonIDs) {
    setDatasetID(1, datasetID)
    setLong(2, it)
  }
}
