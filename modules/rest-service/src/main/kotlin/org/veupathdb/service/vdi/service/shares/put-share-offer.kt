package org.veupathdb.service.vdi.service.shares

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.generated.model.DatasetShareOffer
import org.veupathdb.service.vdi.generated.model.ShareOfferAction
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareOffer
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareReceipt
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import org.veupathdb.vdi.lib.db.cache.CacheDB

internal fun putShareOffer(datasetID: DatasetID, ownerID: UserID, recipientID: UserID, entity: DatasetShareOffer) {
  val dataset = CacheDB.selectDataset(datasetID) ?: throw NotFoundException()

  if (ownerID != dataset.ownerID)
    throw ForbiddenException("cannot offer a share to a dataset you do not own")

  DatasetStore.putShareOffer(ownerID, datasetID, recipientID, entity.toInternal())

  // We automatically accept share offers on behalf of the recipient user.  That
  // user can reject the share later if they so choose.
  DatasetStore.putShareReceipt(ownerID, datasetID, recipientID, VDIDatasetShareReceipt(VDIShareReceiptAction.Accept))
}

private fun DatasetShareOffer.toInternal() =
  VDIDatasetShareOffer(when (action) {
    null                    -> throw BadRequestException("share action is required")
    ShareOfferAction.GRANT  -> VDIShareOfferAction.Grant
    ShareOfferAction.REVOKE -> VDIShareOfferAction.Revoke
  })

