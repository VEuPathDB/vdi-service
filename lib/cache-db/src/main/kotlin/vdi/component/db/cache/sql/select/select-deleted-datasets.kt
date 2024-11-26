package vdi.component.db.cache.sql.select

import vdi.component.db.cache.model.DeletedDataset
import vdi.component.db.cache.util.*
import vdi.component.db.cache.util.getDataType
import vdi.component.db.cache.util.getProjectIDList
import vdi.component.db.cache.util.getUserID
import vdi.component.db.cache.util.map
import java.sql.Connection

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
  createStatement().use { stmt ->
    stmt.executeQuery(SQL).use { rs ->
      rs.map { DeletedDataset(
        it.getDatasetID("dataset_id"),
        it.getUserID("owner_id"),
        it.getProjectIDList("projects"),
        it.getDataType("type_name")
      ) }
    }
  }

