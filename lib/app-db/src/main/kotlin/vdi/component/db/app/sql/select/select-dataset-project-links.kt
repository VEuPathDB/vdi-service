package vdi.component.db.app.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.DatasetProjectLinkRecord
import vdi.component.db.app.sql.setDatasetID
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

internal fun Connection.selectDatasetProjectLinks(schema: String, datasetID: DatasetID): List<DatasetProjectLinkRecord> =
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setDatasetID(1, datasetID)
      ps.executeQuery()
        .use { rs ->
          val out = ArrayList<DatasetProjectLinkRecord>()

          while (rs.next()) {
            out.add(DatasetProjectLinkRecord(
              datasetID = datasetID,
              projectID = rs.getString("project_id")
            ))
          }

          out
        }
    }
