@file:JvmMultifileClass
@file:JvmName("DatasetShareService")
package vdi.service.rest.server.services.shares

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareReceipt
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.model.DatasetShareReceiptImpl
import vdi.lib.db.cache.withTransaction
import vdi.service.rest.generated.model.DatasetShareReceipt
import vdi.service.rest.generated.model.ShareReceiptAction
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.ForbiddenError
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.util.Either
import vdi.service.rest.util.leftOr
import vdi.service.rest.util.mapLeft
import vdi.service.rest.generated.resources.DatasetsVdiIdSharesRecipientUserId.PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse as PutReceipt

fun <T: ControllerBase> T.putShareReceipt(datasetID: DatasetID, recipientID: UserID, entity: DatasetShareReceipt): PutReceipt {
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
    .mapLeft(::VDIDatasetShareReceipt)
    .leftOr { return it }

  cacheDB.withTransaction {
    // Put a share receipt object into S3
    DatasetStore.putShareReceipt(dataset.ownerID, datasetID, recipientID, internal)
    it.upsertDatasetShareReceipt(DatasetShareReceiptImpl(datasetID, recipientID, internal.action))
  }

  return PutReceipt.respond204()
}

private fun DatasetShareReceipt.toInternal(): Either<VDIShareReceiptAction, PutReceipt> =
  when (action) {
    null                      -> Either.ofRight(BadRequestError("share action is required").wrap())
    ShareReceiptAction.ACCEPT -> Either.ofLeft(VDIShareReceiptAction.Accept)
    ShareReceiptAction.REJECT -> Either.ofLeft(VDIShareReceiptAction.Reject)
  }

