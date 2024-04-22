package vdi.component.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.model.DeletedDataset
import vdi.component.db.cache.util.getProjectIDList
import vdi.component.db.cache.util.map
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  dataset_id
, owner_id
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = vd.dataset_id) AS projects
FROM
  vdi.datasets AS vd
WHERE
  is_deleted = TRUE
"""

internal fun Connection.selectDeletedDatasets(): List<DeletedDataset> =
  createStatement().use { stmt ->
    stmt.executeQuery(SQL).use { rs ->
      rs.map { DeletedDataset(
        DatasetID(it.getString("dataset_id")),
        UserID(it.getString("owner_id")),
        it.getProjectIDList("projects"),
      ) }
    }
  }

