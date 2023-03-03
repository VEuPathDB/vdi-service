package vdi.components.datasets

import vdi.components.common.fields.UserID

internal data class DatasetShareImpl(
  override val recipientID: UserID,
  override val offer: DatasetShareOfferFile,
  override val receipt: DatasetShareReceiptFile
) : DatasetShare