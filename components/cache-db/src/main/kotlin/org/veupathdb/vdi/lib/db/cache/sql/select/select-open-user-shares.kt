package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.consts.OfferStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetShareListEntry
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  dso.dataset_id
, ds.owner_id
FROM
  vdi.dataset_share_offers AS dso
  INNER JOIN vdi.datasets  AS ds
    USING (dataset_id)
WHERE
  recipient_id = ?
  AND status = '${OfferStatus.Granted}'
  AND NOT EXISTS(
    SELECT
      1
    FROM
      vdi.dataset_share_receipts AS dsr
    WHERE
      dso.dataset_id = dsr.dataset_id
      AND dso.recipient_id = dsr.recipient_id
  )
"""

internal fun Connection.selectOpenSharesFor(userID: UserID): List<DatasetShareListEntry> {
  val out = ArrayList<DatasetShareListEntry>()

  prepareStatement(SQL).use { ps ->
    ps.setString(1, userID.toString())
    ps.executeQuery().use { rs ->
      while (rs.next()) {
        out.add(
          DatasetShareListEntry(
            datasetID = DatasetID(rs.getString("dataset_id")),
            ownerID   = UserID(rs.getString("owner_id")),
          )
        )
      }
    }
  }

  return out
}

