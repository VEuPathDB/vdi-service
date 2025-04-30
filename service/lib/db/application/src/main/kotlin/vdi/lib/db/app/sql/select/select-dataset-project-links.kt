package vdi.lib.db.app.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.lib.db.app.model.DatasetProjectLinkRecord
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
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
          projectID = it.getString("project_id")
        )
      }
    }
  }
