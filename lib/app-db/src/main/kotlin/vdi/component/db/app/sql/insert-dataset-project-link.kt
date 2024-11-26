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

internal fun Connection.insertDatasetProjectLink(schema: String, datasetID: DatasetID, projectID: ProjectID) {
  preparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, projectID)
  }
}
