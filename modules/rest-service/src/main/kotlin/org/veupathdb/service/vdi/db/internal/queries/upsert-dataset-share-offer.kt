package org.veupathdb.service.vdi.db.internal.queries

import org.veupathdb.service.vdi.generated.model.ShareOfferAction
import java.sql.Connection
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

private const val STATUS_ENUM_GRANT_VALUE = "grant"
private const val STATUS_ENUM_REVOKE_VALUE = "revoke"

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_share_offers (dataset_id, recipient_id, status)
VALUES
  (?, ?, ?)
ON CONFLICT DO UPDATE
SET
  status = ?
"""

internal fun Connection.upsertDatasetShareOffer(
  datasetID: DatasetID,
  recipientID: UserID,
  status: ShareOfferAction,
) {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.setString(2, recipientID.toString())
    ps.setString(3, status.toDBStatus())
    ps.setString(4, status.toDBStatus())

    ps.execute()
  }
}

private fun ShareOfferAction.toDBStatus() =
  when (this) {
    ShareOfferAction.GRANT  -> STATUS_ENUM_GRANT_VALUE
    ShareOfferAction.REVOKE -> STATUS_ENUM_REVOKE_VALUE
  }