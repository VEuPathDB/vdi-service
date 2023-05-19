package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.consts.OfferStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetShareListEntry
import org.veupathdb.vdi.lib.db.cache.util.getProjectIDList
import org.veupathdb.vdi.lib.db.cache.util.setUserID
import java.sql.Connection

// language=postgresql
private const val SQL = """
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
  INNER JOIN vdi.datasets  AS ds
    USING (dataset_id)
WHERE
  dso.recipient_id = ?
  AND dso.status = '${OfferStatus.Granted}'
  AND ds.is_deleted = FALSE
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
    ps.setUserID(1, userID)
    ps.executeQuery().use { rs ->
      while (rs.next()) {
        out.add(
          DatasetShareListEntry(
            datasetID     = DatasetID(rs.getString("dataset_id")),
            ownerID       = UserID(rs.getString("owner_id")),
            typeName      = rs.getString("type_name"),
            typeVersion   = rs.getString("type_version"),
            receiptStatus = null,
            projects      = rs.getProjectIDList("projects")
          )
        )
      }
    }
  }

  return out
}

