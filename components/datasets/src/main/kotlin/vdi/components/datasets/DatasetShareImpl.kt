package vdi.components.datasets

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.components.common.DatasetID
import vdi.components.datasets.model.GrantObject
import vdi.components.datasets.model.ReceiptObject
import vdi.components.json.JSON

internal class DatasetShareImpl(
  private  val ownerID:     Long,
  private  val datasetID: DatasetID,
  override val recipientID: Long,
  private  val bucket: S3Bucket,
) : DatasetShare {

  override fun getGrantState() =
    S3Path.shareOwnerStateFile(ownerID, datasetID, recipientID)
      .let { bucket.objects.open(it) ?: throw IllegalStateException("owner-state.json doesn't exist: $it") }
      .stream
      .use { JSON.readValue<GrantObject>(it) }
      .state

  override fun setGrantState(state: DatasetShare.GrantState) {
    bucket.objects.put(
      S3Path.shareOwnerStateFile(ownerID, datasetID, recipientID),
      JSON.writeValueAsBytes(GrantObject(state)).inputStream()
    )
  }

  override fun getGrantTimestamp() =
    S3Path.shareOwnerStateFile(ownerID, datasetID, recipientID)
      .let { bucket.objects.stat(it) ?: throw IllegalStateException("owner-state.json doesn't exist: $it") }
      .lastModified

  override fun getReceiptState() =
    S3Path.shareRecipientStateFile(ownerID, datasetID, recipientID)
      .let { bucket.objects.open(it) ?: throw IllegalStateException("recipient-state.json doesn't exist: $it") }
      .stream
      .use { JSON.readValue<ReceiptObject>(it) }
      .state

  override fun getReceiptTimestamp() =
    S3Path.shareRecipientStateFile(ownerID, datasetID, recipientID)
      .let { bucket.objects.stat(it) ?: throw IllegalStateException("recipient-state.json doesn't exist: $it") }
      .lastModified

  override fun setReceiptState(state: DatasetShare.ReceiptState) {
    bucket.objects.put(
      S3Path.shareRecipientStateFile(ownerID, datasetID, recipientID),
      JSON.writeValueAsBytes(ReceiptObject(state)).inputStream()
    )
  }
}