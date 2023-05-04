package org.veupathdb.service.vdi.service.shares

import jakarta.ws.rs.BadRequestException
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
  val dataset = CacheDB.selectDataset(datasetID) ?: throw NotFoundException()
  DatasetStore.putShareReceipt(dataset.ownerID, datasetID, recipientID, entity.toInternal())
}

private fun DatasetShareReceipt.toInternal() =
  VDIDatasetShareReceipt(when (action) {
    null                      -> throw BadRequestException("share action is required")
    ShareReceiptAction.ACCEPT -> VDIShareReceiptAction.Accept
    ShareReceiptAction.REJECT -> VDIShareReceiptAction.Reject
  })

