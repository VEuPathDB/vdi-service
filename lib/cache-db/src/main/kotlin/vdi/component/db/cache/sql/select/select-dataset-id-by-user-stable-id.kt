package vdi.component.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.util.*
import java.sql.Connection

// language=postgresql
private val SQL = """
SELECT
  dataset_id
FROM
  vdi.datasets
WHERE
  user_stable_id = ?
"""

internal fun Connection.selectDatasetIDByUserStableID(userStableID: String): DatasetID? =
  withPreparedStatement(SQL) {
    setString(1, userStableID)
    withResults {
      if (!next())
        null
      else
        getDatasetID("dataset_id")
    }
  }
