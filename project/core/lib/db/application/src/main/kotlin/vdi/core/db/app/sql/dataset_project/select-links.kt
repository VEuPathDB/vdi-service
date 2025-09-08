package vdi.core.db.app.sql.dataset_project

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.app.model.DatasetProjectLinkRecord
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID

private fun sql(schema: String) =
// language=postgresql
"""
SELECT
  project_id
FROM
  ${schema}.dataset_project
WHERE
  dataset_id = ?
"""

internal fun Connection.selectDatasetProjectLinks(schema: String, datasetID: DatasetID) =
  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)
    withResults {
      map {
        DatasetProjectLinkRecord(
          datasetID = datasetID,
          installTarget = it.getString("project_id")
        )
      }
    }
  }
