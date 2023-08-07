package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.app.model.DatasetProjectLinkRecord
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
      ps.setString(1, datasetID.toString())
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