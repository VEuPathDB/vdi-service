package vdi.components.datasets.paths

import vdi.components.datasets.util.PathBuilder

@Suppress("NOTHING_TO_INLINE")
internal object S3Paths {

  private const val DELETE_FLAG_FILE_NAME = "delete-flag"
  private const val MANIFEST_FILE_NAME = "manifest.json"
  private const val META_FILE_NAME = "meta.json"
  private const val SHARE_OFFER_FILE_NAME = "offer.json"
  private const val SHARE_RECEIPT_FILE_NAME = "receipt.json"


  // region Public API

  fun rootDir(root: String) =
    (rootPath(root) / "").toString()

  fun userDir(root: String, userID: String) =
    (userPath(root, userID) / "").toString()

  fun datasetDir(root: String, userID: String, datasetID: String) =
    (datasetPath(root, userID, datasetID) / "").toString()


  fun datasetManifestFile(root: String, userID: String, datasetID: String) =
    (datasetPath(root, userID, datasetID) / MANIFEST_FILE_NAME).toString()

  fun datasetMetaFile(root: String, userID: String, datasetID: String) =
    (datasetPath(root, userID, datasetID) / META_FILE_NAME).toString()

  fun datasetDeleteFlagFile(root: String, userID: String, datasetID: String) =
    (datasetPath(root, userID, datasetID) / DELETE_FLAG_FILE_NAME).toString()


  fun datasetDataDir(root: String, userID: String, datasetID: String) =
    (dataPath(root, userID, datasetID) / "").toString()

  fun datasetDataFile(root: String, userID: String, datasetID: String, fileName: String) =
    (dataPath(root, userID, datasetID) / fileName).toString()


  fun datasetSharesDir(root: String, userID: String, datasetID: String) =
    (sharePath(root, userID, datasetID) / "").toString()

  fun datasetShareOfferFile(root: String, userID: String, datasetID: String, recipientID: String) =
    (shareUserPath(root, userID, datasetID, recipientID) / SHARE_OFFER_FILE_NAME).toString()

  fun datasetShareReceiptFile(root: String, userID: String, datasetID: String, recipientID: String) =
    (shareUserPath(root, userID, datasetID, recipientID) / SHARE_RECEIPT_FILE_NAME).toString()


  fun datasetUploadsDir(root: String, userID: String, datasetID: String) =
    (uploadPath(root, userID, datasetID) / "").toString()

  fun datasetUploadFile(root: String, userID: String, datasetID: String, fileName: String) =
    (uploadPath(root, userID, datasetID) / fileName).toString()

  // endregion Public API


  // region Path "Constants"

  private inline fun rootPath(root: String) =
    PathBuilder(root)

  private inline fun userPath(root: String, userID: String) =
    rootPath(root) / userID

  private inline fun datasetPath(root: String, userID: String, datasetID: String) =
    userPath(root, userID) / datasetID

  private inline fun uploadPath(root: String, userID: String, datasetID: String) =
    datasetPath(root, userID, datasetID) / "uploads"

  private inline fun dataPath(root: String, userID: String, datasetID: String) =
    datasetPath(root, userID, datasetID) / "data"

  private inline fun sharePath(root: String, userID: String, datasetID: String) =
    datasetPath(root, userID, datasetID) / "shares"

  private inline fun shareUserPath(root: String, userID: String, datasetID: String, shareUserID: String) =
    sharePath(root, userID, datasetID) / shareUserID

  // endregion Path "Constants"
}

