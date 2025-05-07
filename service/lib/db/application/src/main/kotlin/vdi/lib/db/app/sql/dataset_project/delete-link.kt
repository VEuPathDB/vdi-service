package vdi.lib.db.app.sql.dataset_project

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_project
WHERE
  dataset_id = ?
  AND project_id = ?
"""

internal fun Connection.deleteDatasetProjectLink(schema: String, datasetID: DatasetID, projectID: ProjectID) =
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, projectID)
  }
