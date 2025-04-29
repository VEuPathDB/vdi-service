package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
enum class S3File(val baseName: String) {
  /**
   * Name of the flag object used to indicate that a dataset has soft-deleted.
   */
  DeleteFlagFileName("delete-flag"),

  /**
   * Name of the dataset upload archive that has been uploaded, safety checked,
   * repacked, and is available to be run through import processing.
   */
  ImportReadyZipName("import-ready.zip"),

  /**
   * Name of the import-processed dataset archive that has been validated and
   * transformed, and is available to be installed into target applications.
   */
  InstallReadyZipName("install-ready.zip"),

  MetadataFileName(DatasetMetaFilename),

  Manifest(DatasetManifestFilename),

  /**
   * Name of the raw user upload file that is awaiting safety checking and
   * repacking into an import ready archive.
   */
  RawUploadZip("raw-upload.zip"),

  /**
   * Name of the flag object used to indicate that a dataset has been revised.
   */
  RevisionFlag("revised-flag"),

  SharesDir("shares"),

  ShareOffer("share-offer.json"),

  ShareReceipt("share-receipt.json"),
  ;

}

