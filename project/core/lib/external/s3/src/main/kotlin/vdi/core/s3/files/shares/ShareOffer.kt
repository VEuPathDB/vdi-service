package vdi.core.s3.files.shares

import vdi.core.s3.files.FileName
import vdi.model.meta.DatasetShareOffer

interface ShareOffer: ShareFile<DatasetShareOffer> {
  override val baseName: String
    get() = FileName.ShareOfferFile
}

