package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.app.model.DatasetProjectLinkRecord
import java.sql.Connection

// language=oracle
private const val SQL = """
SELECT
  project_id
FROM
  vdi.dataset_projects
WHERE
  dataset_id = ?
"""

internal fun Connection.selectDatasetProjectLinks(datasetID: DatasetID): List<DatasetProjectLinkRecord> =
  prepareStatement(SQL)
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