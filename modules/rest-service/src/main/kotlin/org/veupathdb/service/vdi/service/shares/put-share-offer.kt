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
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus

internal fun adminPutShareOffer(datasetID: DatasetID, recipientID: UserID, entity: DatasetShareOffer) {
  val dataset = CacheDB.selectDataset(datasetID) ?: throw NotFoundException()

  // If the dataset has been deleted, then it isn't sharable, throw a 403.
  if (dataset.isDeleted)
    throw ForbiddenException("cannot share a deleted dataset")

  if (dataset.importStatus != DatasetImportStatus.Complete)
    throw ForbiddenException("cannot share a dataset until after it has been processed")

  // Write or overwrite the share offer object.
  DatasetStore.putShareOffer(dataset.ownerID, datasetID, recipientID, entity.toInternal())

  // We automatically accept share offers on behalf of the recipient user.  The
  // recipient user can reject the share later if they so choose.
  DatasetStore.putShareReceipt(dataset.ownerID, datasetID, recipientID, VDIDatasetShareReceipt(VDIShareReceiptAction.Accept))
}

internal fun putShareOffer(datasetID: DatasetID, ownerID: UserID, recipientID: UserID, entity: DatasetShareOffer) {
  // Lookup the target dataset or throw a 404 if it doesn't exist.
  val dataset = CacheDB.selectDataset(datasetID)
    ?: throw NotFoundException("no such dataset")

  // If the dataset is not owned by the requesting user, throw a 403
  if (ownerID != dataset.ownerID)
    throw ForbiddenException("cannot offer a share to a dataset you do not own")

  // If the dataset has been deleted, then it isn't sharable, throw a 403.
  if (dataset.isDeleted)
    throw ForbiddenException("cannot share a deleted dataset")

  if (dataset.importStatus != DatasetImportStatus.Complete)
    throw ForbiddenException("cannot share a dataset until after it has been processed")

  // Write or overwrite the share offer object.
  DatasetStore.putShareOffer(ownerID, datasetID, recipientID, entity.toInternal())

  // We automatically accept share offers on behalf of the recipient user.  The
  // recipient user can reject the share later if they so choose.
  DatasetStore.putShareReceipt(ownerID, datasetID, recipientID, VDIDatasetShareReceipt(VDIShareReceiptAction.Accept))
}

private fun DatasetShareOffer.toInternal() =
  VDIDatasetShareOffer(when (action) {
    null                    -> throw BadRequestException("share action is required")
    ShareOfferAction.GRANT  -> VDIShareOfferAction.Grant
    ShareOfferAction.REVOKE -> VDIShareOfferAction.Revoke
  })

