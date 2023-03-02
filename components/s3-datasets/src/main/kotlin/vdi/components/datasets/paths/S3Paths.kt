package vdi.components.datasets.paths

import vdi.components.datasets.util.PathBuilder

@Suppress("NOTHING_TO_INLINE")
object S3Paths {

  const val ROOT_DIR_NAME = "vdi"
  const val UPLOAD_DIR_NAME = "upload"
  const val DATASET_DIR_NAME = "dataset"
  const val DATA_DIR_NAME = "data"
  const val SHARES_DIR_NAME = "shares"

  const val DELETE_FLAG_FILE_NAME = "delete-flag"
  const val MANIFEST_FILE_NAME = "manifest.json"
  const val META_FILE_NAME = "meta.json"
  const val SHARE_OFFER_FILE_NAME = "offer.json"
  const val SHARE_RECEIPT_FILE_NAME = "receipt.json"


  // region Public API

  /**
   * `{root}/`
   */
  fun rootDir() =
    (rootPath() / "").toString()

  /**
   * `{root}/{user-id}/`
   */
  fun userDir(userID: String) =
    (userPath(userID) / "").toString()

  /**
   * `{root}/{user-id}/{dataset-id}`
   */
  fun datasetDir(userID: String, datasetID: String) =
    (datasetPath(userID, datasetID) / "").toString()


  fun datasetManifestFile(userID: String, datasetID: String) =
    (datasetContentPath(userID, datasetID) / MANIFEST_FILE_NAME).toString()

  fun datasetMetaFile(userID: String, datasetID: String) =
    (datasetContentPath(userID, datasetID) / META_FILE_NAME).toString()

  fun datasetDeleteFlagFile(userID: String, datasetID: String) =
    (datasetContentPath(userID, datasetID) / DELETE_FLAG_FILE_NAME).toString()


  fun datasetDataDir(userID: String, datasetID: String) =
    (dataPath(userID, datasetID) / "").toString()

  fun datasetDataFile(userID: String, datasetID: String, fileName: String) =
    (dataPath(userID, datasetID) / fileName).toString()


  fun datasetSharesDir(userID: String, datasetID: String) =
    (sharePath(userID, datasetID) / "").toString()

  fun datasetShareOfferFile(userID: String, datasetID: String, recipientID: String) =
    (shareUserPath(userID, datasetID, recipientID) / SHARE_OFFER_FILE_NAME).toString()

  fun datasetShareReceiptFile(userID: String, datasetID: String, recipientID: String) =
    (shareUserPath(userID, datasetID, recipientID) / SHARE_RECEIPT_FILE_NAME).toString()


  fun datasetUploadsDir(userID: String, datasetID: String) =
    (uploadPath(userID, datasetID) / "").toString()

  fun datasetUploadFile(userID: String, datasetID: String, fileName: String) =
    (uploadPath(userID, datasetID) / fileName).toString()

  // endregion Public API


  // region Path "Constants"

  private inline fun rootPath() =
    PathBuilder(ROOT_DIR_NAME)

  private inline fun userPath(userID: String) =
    rootPath() / userID

  private inline fun datasetPath(userID: String, datasetID: String) =
    userPath(userID) / datasetID

  private inline fun datasetContentPath(userID: String, datasetID: String) =
    datasetPath(userID, datasetID) / DATASET_DIR_NAME

  private inline fun uploadPath(userID: String, datasetID: String) =
    datasetPath(userID, datasetID) / UPLOAD_DIR_NAME

  private inline fun dataPath(userID: String, datasetID: String) =
    datasetContentPath(userID, datasetID) / DATA_DIR_NAME

  private inline fun sharePath(userID: String, datasetID: String) =
    datasetContentPath(userID, datasetID) / SHARES_DIR_NAME

  private inline fun shareUserPath(userID: String, datasetID: String, shareUserID: String) =
    sharePath(userID, datasetID) / shareUserID

  // endregion Path "Constants"
}

