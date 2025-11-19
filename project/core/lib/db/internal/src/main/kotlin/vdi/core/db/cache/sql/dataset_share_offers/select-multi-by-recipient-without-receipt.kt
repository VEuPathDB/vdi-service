package vdi.core.db.cache.sql.dataset_share_offers

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.meta.UserID
import java.sql.Connection
import vdi.core.db.cache.model.DatasetShareListEntry
import vdi.core.db.cache.util.getProjectIDList
import vdi.core.db.jdbc.getDataType
import vdi.core.db.jdbc.getUserID
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setUserID
import vdi.model.meta.DatasetShareOffer
import vdi.model.meta.DatasetType

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
  INNER JOIN vdi.datasets  AS ds
    USING (dataset_id)
WHERE
  dso.recipient_id = ?
  AND dso.status = '${DatasetShareOffer.Action.Grant}'
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

internal fun Connection.selectOpenSharesFor(userID: UserID) =
  withPreparedStatement(SQL) {
    setUserID(1, userID)
    withResults {
      map {
        DatasetShareListEntry(
          datasetID     = it.reqDatasetID("dataset_id"),
          ownerID       = it.getUserID("owner_id"),
          type          = DatasetType(it.getDataType("type_name"), it.getString("type_version")),
          receiptStatus = null,
          projects      = it.getProjectIDList("projects")
        )
      }
    }
  }
