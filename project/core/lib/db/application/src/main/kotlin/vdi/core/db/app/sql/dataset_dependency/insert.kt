package vdi.core.db.app.sql.dataset_dependency

import io.foxcapades.kdbc.withPreparedBatchUpdate
import vdi.model.data.DatasetID
import vdi.model.data.DatasetDependency
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID


private fun sql(schema: String) =
// language=oracle
  """
INSERT INTO
  ${schema}.dataset_dependency (
    dataset_id
  , identifier
  , display_name
  , version
  )
VALUES
  (?, ?, ?, ?)
"""

internal fun Connection.insertDatasetDependencies(
  schema: String,
  datasetID: DatasetID,
  dependencies: Collection<DatasetDependency>,
) {
  withPreparedBatchUpdate(sql(schema), dependencies) {
    setDatasetID(1, datasetID)
    setString(2, it.identifier)
    setString(3, it.displayName)
    setString(4, it.version)
  }
}
