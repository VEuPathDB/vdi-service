package vdi.lib.db.app.sql.dataset_project

import io.foxcapades.kdbc.withPreparedBatchUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import java.sql.Connection

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
  projectIDs: Iterable<ProjectID>
) {
  withPreparedBatchUpdate(sql(schema), projectIDs) {
    setString(1, datasetID.toString())
    setString(2, it)
  }
}
