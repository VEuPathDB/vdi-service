package vdi.core.db.app.sql.dataset_project

import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.dataset_project (
    dataset_id
  , project_id
  )
VALUES
  (?, ?)
"""

internal fun Connection.insertDatasetProjectLinks(
  schema: String,
  datasetID: DatasetID,
  projectIDs: Iterable<InstallTargetID>
) {
  withPreparedBatchUpdate(sql(schema), projectIDs) {
    setString(1, datasetID.toString())
    setString(2, it)
  }
}
