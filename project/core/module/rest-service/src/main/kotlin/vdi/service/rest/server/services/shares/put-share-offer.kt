@file:JvmMultifileClass
@file:JvmName("DatasetShareService")
package vdi.service.rest.server.services.shares

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotFoundException
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.ShareOfferRecord
import vdi.core.db.cache.model.ShareReceiptRecord
import vdi.core.db.cache.withTransaction
import vdi.model.data.DatasetID
import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt
import vdi.model.data.UserID
import vdi.service.rest.generated.model.ShareOfferAction
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.outputs.ForbiddenError
import vdi.service.rest.server.outputs.NotFoundError
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.generated.model.DatasetShareOffer as APIShare
import vdi.service.rest.generated.resources.DatasetsVdiIdSharesRecipientUserId.PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse as PutOffer

fun adminPutShareOffer(datasetID: DatasetID, recipientID: UserID, entity: APIShare): PutOffer {
  val dataset = CacheDB().selectDataset(datasetID) ?: throw NotFoundException()

  // If the dataset has been deleted, then it isn't sharable, throw a 403.
  if (dataset.isDeleted)
    return ForbiddenError("cannot share a deleted dataset").wrap()

  if (dataset.importStatus != DatasetImportStatus.Complete)
    return ForbiddenError("cannot share a dataset until after it has been processed").wrap()

  // Write or overwrite the share offer object.
  DatasetStore.putShareOffer(dataset.ownerID, datasetID, recipientID, entity.toInternal())

  // We automatically accept share offers on behalf of the recipient user.  The
  // recipient user can reject the share later if they so choose.
  DatasetStore.putShareReceipt(
    dataset.ownerID,
    datasetID,
    recipientID,
    DatasetShareReceipt(DatasetShareReceipt.Action.Accept)
  )

  return PutOffer.respond204()
}

fun <T: ControllerBase> T.putShareOffer(datasetID: DatasetID, recipientID: UserID, entity: APIShare): PutOffer {
  val ownerID = userID // For clarity

  // Lookup the target dataset or throw a 404 if it doesn't exist.
  val dataset = CacheDB().selectDataset(datasetID)
    ?: return NotFoundError("no such dataset").wrap()

  // If the dataset is not owned by the requesting user, throw a 403
  if (ownerID != dataset.ownerID)
    return ForbiddenError("cannot offer a share to a dataset you do not own").wrap()

  // If the dataset has been deleted, then it isn't sharable, throw a 403.
  if (dataset.isDeleted)
    return ForbiddenError("cannot share a deleted dataset").wrap()

  when (dataset.importStatus) {
    DatasetImportStatus.Queued, DatasetImportStatus.InProgress,
      -> return ForbiddenError("cannot share a dataset until after it has been processed").wrap()

    DatasetImportStatus.Invalid, DatasetImportStatus.Failed,
      -> return ForbiddenError("cannot share a dataset whose import failed").wrap()

    DatasetImportStatus.Complete -> { /* Do nothing */ }
  }

  val existingShareReceipt = CacheDB().selectSharesForDataset(datasetID)
    .find { it.recipientID == recipientID }

  // Short circuit the normal flow from MinIO to the share handler for insertion
  // to postgres on this campus.  This way the client can reflect the change
  // immediately.
  CacheDB().withTransaction {
    val internal = entity.toInternal()

    // Write or overwrite the share offer object.
    DatasetStore.putShareOffer(ownerID, datasetID, recipientID, internal)
    it.upsertDatasetShareOffer(ShareOfferRecord(datasetID, recipientID, internal.action))

    if (existingShareReceipt == null) {
      // We automatically accept share offers on behalf of the recipient user.  The
      // recipient user can reject the share later if they so choose.
      DatasetStore.putShareReceipt(
        ownerID,
        datasetID,
        recipientID,
        DatasetShareReceipt(DatasetShareReceipt.Action.Accept)
      )

      it.upsertDatasetShareReceipt(ShareReceiptRecord(datasetID, recipientID, DatasetShareReceipt.Action.Accept))
    }
  }

  return PutOffer.respond204()
}

private fun APIShare.toInternal() =
  DatasetShareOffer(when (action) {
    null                    -> throw BadRequestException("share action is required")
    ShareOfferAction.GRANT  -> DatasetShareOffer.Action.Grant
    ShareOfferAction.REVOKE -> DatasetShareOffer.Action.Revoke
  })

