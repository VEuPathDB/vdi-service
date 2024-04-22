package vdi.component.s3.files

import org.veupathdb.vdi.lib.common.field.UserID

internal data class DatasetShareImpl(
  override val recipientID: UserID,
  override val offer: DatasetShareOfferFile,
  override val receipt: DatasetShareReceiptFile
) : DatasetShare