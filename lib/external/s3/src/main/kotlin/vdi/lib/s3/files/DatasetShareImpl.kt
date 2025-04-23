package vdi.lib.s3.files

import org.veupathdb.vdi.lib.common.field.UserID

internal data class DatasetShareImpl(
  override val recipientID: UserID,
  override val offer: vdi.lib.s3.files.DatasetShareOfferFile,
  override val receipt: DatasetShareReceiptFile
) : DatasetShare
