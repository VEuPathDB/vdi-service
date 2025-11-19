package vdi.core.s3.files.shares

import vdi.core.s3.files.FileName
import vdi.model.meta.DatasetShareReceipt

interface ShareReceipt: ShareFile<DatasetShareReceipt> {
  override val baseName: String
    get() = FileName.ShareReceiptFile
}
