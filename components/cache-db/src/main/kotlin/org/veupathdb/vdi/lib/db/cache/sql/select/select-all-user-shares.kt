package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import org.veupathdb.vdi.lib.db.cache.consts.OfferStatus
import org.veupathdb.vdi.lib.db.cache.consts.ReceiptStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetShareListEntry
import org.veupathdb.vdi.lib.db.cache.util.gatherProjectIDs
import java.sql.Connection

// language=postgresql
private const val SQL = """
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
  AND dso.status = '${OfferStatus.Granted}'
"""

internal fun Connection.selectAllSharesFor(userID: UserID): List<DatasetShareListEntry> {
  val out = ArrayList<DatasetShareListEntry>()

  prepareStatement(SQL).use { ps ->
    ps.setString(1, userID.toString())
    ps.executeQuery().use { rs ->
      while (rs.next()) {
        out.add(
          DatasetShareListEntry(
            datasetID     = DatasetID(rs.getString("dataset_id")),
            ownerID       = UserID(rs.getString("owner_id")),
            typeName      = rs.getString("type_name"),
            typeVersion   = rs.getString("type_version"),
            receiptStatus = rs.getString("status")?.let(VDIShareReceiptAction::fromString),
            projects      = rs.getArray("projects").gatherProjectIDs()
          )
        )
      }
    }
  }

  return out
}
