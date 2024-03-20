package vdi.component.db.app.sql

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
  prepareStatement(sql(schema))
    .use { ps ->
      for (projectID in projectIDs) {
        ps.setString(1, datasetID.toString())
        ps.setString(2, projectID)
        ps.addBatch()
      }

      ps.executeBatch()
    }
}