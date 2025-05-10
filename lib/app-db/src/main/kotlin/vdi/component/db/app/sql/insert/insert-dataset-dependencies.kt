package vdi.component.db.app.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetDependency
import vdi.component.db.app.sql.preparedBatchUpdate
import vdi.component.db.app.sql.setDatasetID
import java.sql.Connection


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
  dependencies: Collection<VDIDatasetDependency>,
) {
  preparedBatchUpdate(sql(schema), dependencies) {
    setDatasetID(1, datasetID)
    setString(2, it.identifier)
    setString(3, it.displayName)
    setString(4, it.version)
  }
}
