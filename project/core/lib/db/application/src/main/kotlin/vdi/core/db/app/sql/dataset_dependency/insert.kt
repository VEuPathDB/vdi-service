package vdi.core.db.app.sql.dataset_dependency

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.set
import vdi.model.data.DatasetDependency
import vdi.model.data.DatasetID


private fun sql(schema: String) =
// language=postgresql
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
    set(1, datasetID)
    set(2, it.identifier)
    set(3, it.displayName)
    set(4, it.version)
  }
}
