package vdi.core.db.cache.sql.datasets

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.sql.Connection
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setUserID

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
    withResults { map { reqDatasetID("dataset_id") } }
  }
}
