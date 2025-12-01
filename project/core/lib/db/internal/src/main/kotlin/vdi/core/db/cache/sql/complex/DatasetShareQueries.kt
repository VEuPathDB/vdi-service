package vdi.core.db.cache.sql.complex

import io.foxcapades.kdbc.forEach
import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.cache.model.DatasetShare
import vdi.core.db.cache.model.DatasetShareListEntry
import vdi.core.db.cache.util.getProjectIDList
import vdi.core.db.jdbc.*
import vdi.model.meta.*

internal object DatasetShareQueries {

  // language=postgresql
  private const val SelectBySingleSQL = """
SELECT
  dso.recipient_id
, dso.status AS offer_status
, dsr.status AS receipt_status
FROM
  vdi.dataset_share_offers AS dso
  FULL OUTER JOIN vdi.dataset_share_receipts AS dsr
    USING (dataset_id, recipient_id)
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun select(datasetID: DatasetID) =
    con.withPreparedStatement(SelectBySingleSQL) {
      setDatasetID(1, datasetID)
      withResults {
        map {
          DatasetShare(
            recipientID   = it.getUserID("recipient_id"),
            offerStatus   = it.getString("offer_status")?.let(DatasetShareOffer.Action::fromString),
            receiptStatus = it.getString("receipt_status")?.let(DatasetShareReceipt.Action::fromString)
          )
        }
      }
    }

  // language=postgresql
  private const val SelectByMultiSQL = """
SELECT
  ids.dataset_id
, dso.recipient_id
, dso.status AS offer_status
, dsr.status AS receipt_status
FROM
  unnest(?) AS ids(dataset_id)
  INNER JOIN vdi.dataset_share_offers AS dso
    USING (dataset_id)
  LEFT JOIN vdi.dataset_share_receipts AS dsr
    USING (dataset_id, recipient_id)
"""

  context(con: Connection)
  internal fun select(datasetIDs: List<DatasetID>): Map<DatasetID, List<DatasetShare>> {
    val out = HashMap<DatasetID, MutableList<DatasetShare>>(datasetIDs.size)

    con.withPreparedStatement(SelectByMultiSQL) {
      setArray(1, con.createArrayOf("varchar", datasetIDs.toTypedArray()))
      withResults {
        forEach {
          val dsID = reqDatasetID("dataset_id")
          out.computeIfAbsent(dsID) { ArrayList() }.add(DatasetShare(
            recipientID   = getUserID("recipient_id"),
            offerStatus   = DatasetShareOffer.Action.fromString(getString("offer_status")),
            receiptStatus = getString("receipt_status")?.let(DatasetShareReceipt.Action::fromString) ?: DatasetShareReceipt.Action.Accept
          ))
        }
      }
    }

    return out
  }

  // language=postgresql
  private val SelectByRecipientSQL = """
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

  context(con: Connection)
  internal fun select(recipientID: UserID) =
    con.withPreparedStatement(SelectByRecipientSQL) {
      setUserID(1, recipientID)
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

  // language=postgresql
  private const val SelectByStatusSQL = """
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

  context(con: Connection)
  internal fun select(
    recipientID: UserID,
    offerStatus: DatasetShareOffer.Action,
    receiptStatus: DatasetShareReceipt.Action,
  ) =
    con.withPreparedStatement(SelectByStatusSQL) {
      setUserID(1, recipientID)
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

  // language=postgresql
  private val SelectOpenSharesSQL = """
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

  context(con: Connection)
  internal fun selectOpenShares(recipientID: UserID) =
    con.withPreparedStatement(SelectOpenSharesSQL) {
      setUserID(1, recipientID)
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
}