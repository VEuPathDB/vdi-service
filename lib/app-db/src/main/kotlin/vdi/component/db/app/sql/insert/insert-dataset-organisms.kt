package vdi.component.db.app.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.sql.preparedBatchUpdate
import vdi.component.db.app.sql.setDatasetID
import java.sql.Connection


private fun sql(schema: String) =
// language=oracle
  """
INSERT INTO
  ${schema}.dataset_organism (
    dataset_id
  , organism_abbrev
  )
VALUES
  (?, ?)
"""

internal fun Connection.insertDatasetOrganisms(
  schema: String,
  datasetID: DatasetID,
  organisms: Iterable<String>,
) {
  preparedBatchUpdate(sql(schema), organisms) {
    setDatasetID(1, datasetID)
    setString(2, it)
  }
}
