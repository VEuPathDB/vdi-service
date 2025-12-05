@file:JvmMultifileClass
@file:JvmName("DatasetShareService")
package vdi.service.rest.server.services.shares

import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.ShareReceiptRecord
import vdi.core.db.cache.withTransaction
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetShareReceipt
import vdi.model.meta.UserID
import vdi.service.rest.generated.model.ShareReceiptAction
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.ForbiddenError
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.wrap
import vdi.util.fn.Either
import vdi.util.fn.leftOr
import vdi.util.fn.mapLeft
import vdi.service.rest.generated.model.DatasetShareReceipt as APIShare
import vdi.service.rest.generated.resources.DatasetsVdiIdSharesRecipientUserId.PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse as PutReceipt

fun putShareReceipt(datasetID: DatasetID, recipientID: UserID, entity: APIShare): PutReceipt {
  val cacheDB = CacheDB()

  // lookup the target dataset or throw a 404 if it doesn't exist
  val dataset = cacheDB.selectDataset(datasetID)
    ?: return Static404.wrap()

  // If the dataset is deleted, throw a 403
  if (dataset.isDeleted)
    return Static404.wrap()

  when (dataset.importStatus) {
    DatasetImportStatus.Queued, DatasetImportStatus.InProgress
    -> return ForbiddenError("cannot share a dataset until after it has been processed").wrap()

    DatasetImportStatus.Invalid, DatasetImportStatus.Failed
    -> return ForbiddenError("cannot share a dataset whose import failed").wrap()

    DatasetImportStatus.Complete -> { /* Do nothing */ }
  }

  val internal = entity.toInternal()
    .mapLeft(::DatasetShareReceipt)
    .leftOr { return it }

  cacheDB.withTransaction {
    // Put a share receipt object into S3
    DatasetStore.putShareReceipt(dataset.ownerID, datasetID, recipientID, internal)
    it.upsertDatasetShareReceipt(ShareReceiptRecord(datasetID, recipientID, internal.action))
  }

  return PutReceipt.respond204()
}

private fun APIShare.toInternal(): Either<DatasetShareReceipt.Action, PutReceipt> =
  when (action) {
    null                      -> Either.ofRight(BadRequestError("share action is required").wrap())
    ShareReceiptAction.ACCEPT -> Either.ofLeft(DatasetShareReceipt.Action.Accept)
    ShareReceiptAction.REJECT -> Either.ofLeft(DatasetShareReceipt.Action.Reject)
  }

