package vdi.core.s3.files

import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt

enum class ShareFileType(val fileName: String) {
  Offer(FileName.ShareOfferFile),
  Receipt(FileName.ShareReceiptFile);

  val contentType get() = "application/json"

  val typeDefinition get() = when (this) {
    Offer   -> DatasetShareOffer::class
    Receipt -> DatasetShareReceipt::class
  }
}
