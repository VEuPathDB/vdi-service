package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withStatementResults
import java.sql.Connection
import vdi.lib.db.cache.model.DeletedDataset
import vdi.lib.db.cache.util.getProjectIDList
import vdi.lib.db.jdbc.getDataType
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.reqDatasetID

// language=postgresql
private const val SQL = """
SELECT
  dataset_id
, owner_id
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = vd.dataset_id) AS projects
, type_name
FROM
  vdi.datasets AS vd
WHERE
  is_deleted = TRUE
"""

internal fun Connection.selectDeletedDatasets(): List<DeletedDataset> =
  createStatement().withStatementResults(SQL) { map {
    DeletedDataset(
      it.reqDatasetID("dataset_id"),
      it.getUserID("owner_id"),
      it.getProjectIDList("projects"),
      it.getDataType("type_name")
    )
  } }
