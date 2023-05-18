package org.veupathdb.service.vdi.service.shares

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.generated.model.DatasetShareReceipt
import org.veupathdb.service.vdi.generated.model.ShareReceiptAction
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareReceipt
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import org.veupathdb.vdi.lib.db.cache.CacheDB

internal fun putShareReceipt(datasetID: DatasetID, recipientID: UserID, entity: DatasetShareReceipt) {
  // lookup the target dataset or throw a 404 if it doesn't exist
  val dataset = CacheDB.selectDataset(datasetID)
    ?: throw NotFoundException()

  // If the dataset is deleted, throw a 403
  if (dataset.isDeleted)
    throw ForbiddenException("cannot accept shares on a deleted dataset")

  // Put a share receipt object into S3
  DatasetStore.putShareReceipt(dataset.ownerID, datasetID, recipientID, entity.toInternal())
}

private fun DatasetShareReceipt.toInternal() =
  VDIDatasetShareReceipt(when (action) {
    null                      -> throw BadRequestException("share action is required")
    ShareReceiptAction.ACCEPT -> VDIShareReceiptAction.Accept
    ShareReceiptAction.REJECT -> VDIShareReceiptAction.Reject
  })

