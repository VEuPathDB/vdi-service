package vdi.component.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.util.*
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  d.dataset_id
FROM
  vdi.datasets AS d
WHERE
  d.owner_id = ?
  AND d.is_deleted = FALSE
"""

internal fun Connection.selectUndeletedDatasetIDsForUser(userID: UserID): List<DatasetID> {
  return withPreparedStatement(SQL) {
    setUserID(1, userID)
    withResults { map { getDatasetID("dataset_id") } }
  }
}
