package vdi.lib.s3.files

import vdi.model.DatasetManifestFilename
import vdi.model.DatasetMetaFilename

object FileName {
  const val DeleteFlagFile = "delete-flag"
  const val RevisedFlagFile = "revised-flag"

  const val RawUploadFile = "raw-upload.zip"
  const val ImportReadyFile = "import-ready.zip"
  const val InstallReadyFile = "install-ready.zip"

  const val MetadataFile = DatasetMetaFilename
  const val ManifestFile = DatasetManifestFilename

  const val ShareOfferFile = "share-offer.json"
  const val ShareReceiptFile = "share-receipt.json"


  const val ShareDirectoryName = "shares"
  const val DocumentDirectoryName = "documents"
}