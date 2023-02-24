package vdi.components.datasets

internal data class DatasetShareImpl(
  override val recipientID: String,
  override val offer: DatasetShareOfferFile,
  override val receipt: DatasetShareReceiptFile
) : DatasetShare