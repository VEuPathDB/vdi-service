package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.model.DeletedDataset
import org.veupathdb.vdi.lib.db.cache.util.map
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  dataset_id
, owner_id
FROM
  vdi.datasets
WHERE
  is_deleted = TRUE
"""

internal fun Connection.selectDeletedDatasets(): List<DeletedDataset> =
  createStatement().use { stmt ->
    stmt.executeQuery(SQL).use { rs ->
      rs.map { DeletedDataset(
        DatasetID(it.getString("dataset_id")),
        UserID(it.getString("owner_id"))
      ) }
    }
  }

