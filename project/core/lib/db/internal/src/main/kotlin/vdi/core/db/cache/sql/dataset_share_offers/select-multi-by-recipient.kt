package vdi.core.db.cache.sql.dataset_share_offers

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.UserID
import java.sql.Connection
import vdi.core.db.cache.model.DatasetShareListEntry
import vdi.core.db.cache.util.getProjectIDList
import vdi.core.db.jdbc.getDataType
import vdi.core.db.jdbc.getUserID
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setUserID
import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt
import vdi.model.data.DatasetType

// language=postgresql
private val SQL = """
SELECT
  dso.dataset_id
, ds.owner_id
, ds.type_name
, ds.type_version
, dsr.status
, ARRAY(
    SELECT
      project_id
    FROM
      vdi.dataset_projects dp
    WHERE
      dso.dataset_id = dp.dataset_id
  ) AS projects
FROM
  vdi.dataset_share_offers AS dso
  INNER JOIN vdi.datasets AS ds
    USING (dataset_id)
  LEFT JOIN vdi.dataset_share_receipts AS dsr
    USING (dataset_id, recipient_id)
WHERE
  dso.recipient_id = ?
  AND ds.is_deleted = FALSE
  AND dso.status = '${DatasetShareOffer.Action.Grant}'
"""

internal fun Connection.selectAllSharesFor(userID: UserID): List<DatasetShareListEntry> {
  return withPreparedStatement(SQL) {
    setUserID(1, userID)
    withResults {
      map {
        DatasetShareListEntry(
          datasetID     = it.reqDatasetID("dataset_id"),
          ownerID       = it.getUserID("owner_id"),
          type          = DatasetType(it.getDataType("type_name"), it.getString("type_version")),
          receiptStatus = it.getString("status")?.let(DatasetShareReceipt.Action::fromString),
          projects      = it.getProjectIDList("projects")
        )
      }
    }
  }
}
