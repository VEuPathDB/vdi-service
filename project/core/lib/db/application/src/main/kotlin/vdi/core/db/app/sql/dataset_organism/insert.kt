package vdi.core.db.app.sql.dataset_organism

import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID


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
  withPreparedBatchUpdate(sql(schema), organisms) {
    setDatasetID(1, datasetID)
    setString(2, it)
  }
}
