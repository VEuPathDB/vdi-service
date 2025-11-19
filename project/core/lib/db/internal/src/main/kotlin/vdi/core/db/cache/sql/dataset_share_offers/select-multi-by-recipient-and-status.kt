package vdi.core.db.cache.sql.dataset_share_offers

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.cache.model.DatasetShareListEntry
import vdi.core.db.cache.util.getProjectIDList
import vdi.core.db.jdbc.*
import vdi.model.meta.DatasetShareOffer
import vdi.model.meta.DatasetShareReceipt
import vdi.model.meta.DatasetType
import vdi.model.meta.UserID

// language=postgresql
private val SQL = """
SELECT
  dso.dataset_id
, ds.owner_id
, ds.type_name
, ds.type_version
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
  INNER JOIN vdi.dataset_share_receipts AS dsr
    USING (dataset_id, recipient_id)
WHERE
  dso.recipient_id = ?
  AND ds.is_deleted = FALSE
  AND dso.status = ?
  AND dsr.status = ?
"""

internal fun Connection.selectSharesFor(
  userID: UserID,
  offerStatus: DatasetShareOffer.Action,
  receiptStatus: DatasetShareReceipt.Action,
) =
  withPreparedStatement(SQL) {
    setUserID(1, userID)
    setString(2, offerStatus.toString())
    setString(3, receiptStatus.toString())

    withResults {
      map {
        DatasetShareListEntry(
          datasetID     = it.reqDatasetID("dataset_id"),
          ownerID       = it.getUserID("owner_id"),
          type          = DatasetType(it.getDataType("type_name"), it.getString("type_version")),
          receiptStatus = receiptStatus,
          projects      = it.getProjectIDList("projects")
        )
      }
    }
  }
