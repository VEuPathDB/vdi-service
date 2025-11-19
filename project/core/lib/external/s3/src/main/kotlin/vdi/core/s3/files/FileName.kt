package vdi.core.s3.files

import vdi.model.DatasetManifestFilename
import vdi.model.DatasetMetaFilename

object FileName {
  // Flag Files
  const val DeleteFlagFile = "delete-flag"
  const val RevisedFlagFile = "revised-flag"

  // Data Files
  const val RawUploadFile = "raw-upload.zip"
  const val ImportReadyFile = "import-ready.zip"
  const val InstallReadyFile = "install-ready.zip"

  inline val DataFileNames get() = arrayOf(ImportReadyFile, InstallReadyFile, RawUploadFile)

  // Meta Files
  const val MetadataFile = DatasetMetaFilename
  const val ManifestFile = DatasetManifestFilename

  inline val MetaFileNames get() = arrayOf(MetadataFile, ManifestFile)

  // Share Files
  const val ShareDirectory = "shares"

  const val ShareOfferFile = "share-offer.json"
  const val ShareReceiptFile = "share-receipt.json"

  // Mapping Files
  const val MappingDirectory = "mappings"

  // Document Files
  const val DocumentDirectory = "documents"

}