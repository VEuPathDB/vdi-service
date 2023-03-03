package vdi.components.datasets.paths

import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID
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
  fun userDir(userID: UserID) =
    (userPath(userID) / "").toString()

  /**
   * `{root}/{user-id}/{dataset-id}`
   */
  fun datasetDir(userID: UserID, datasetID: DatasetID) =
    (datasetPath(userID, datasetID) / "").toString()


  fun datasetManifestFile(userID: UserID, datasetID: DatasetID) =
    (datasetContentPath(userID, datasetID) / MANIFEST_FILE_NAME).toString()

  fun datasetMetaFile(userID: UserID, datasetID: DatasetID) =
    (datasetContentPath(userID, datasetID) / META_FILE_NAME).toString()

  fun datasetDeleteFlagFile(userID: UserID, datasetID: DatasetID) =
    (datasetContentPath(userID, datasetID) / DELETE_FLAG_FILE_NAME).toString()


  fun datasetDataDir(userID: UserID, datasetID: DatasetID) =
    (dataPath(userID, datasetID) / "").toString()

  fun datasetDataFile(userID: UserID, datasetID: DatasetID, fileName: String) =
    (dataPath(userID, datasetID) / fileName).toString()


  fun datasetSharesDir(userID: UserID, datasetID: DatasetID) =
    (sharePath(userID, datasetID) / "").toString()

  fun datasetShareOfferFile(userID: UserID, datasetID: DatasetID, recipientID: UserID) =
    (shareUserPath(userID, datasetID, recipientID) / SHARE_OFFER_FILE_NAME).toString()

  fun datasetShareReceiptFile(userID: UserID, datasetID: DatasetID, recipientID: UserID) =
    (shareUserPath(userID, datasetID, recipientID) / SHARE_RECEIPT_FILE_NAME).toString()


  fun datasetUploadsDir(userID: UserID, datasetID: DatasetID) =
    (uploadPath(userID, datasetID) / "").toString()

  fun datasetUploadFile(userID: UserID, datasetID: DatasetID, fileName: String) =
    (uploadPath(userID, datasetID) / fileName).toString()

  // endregion Public API


  // region Path "Constants"

  private inline fun rootPath() =
    PathBuilder(ROOT_DIR_NAME)

  private inline fun userPath(userID: UserID) =
    rootPath() / userID.toString()

  private inline fun datasetPath(userID: UserID, datasetID: DatasetID) =
    userPath(userID) / datasetID.toString()

  private inline fun datasetContentPath(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID) / DATASET_DIR_NAME

  private inline fun uploadPath(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID) / UPLOAD_DIR_NAME

  private inline fun dataPath(userID: UserID, datasetID: DatasetID) =
    datasetContentPath(userID, datasetID) / DATA_DIR_NAME

  private inline fun sharePath(userID: UserID, datasetID: DatasetID) =
    datasetContentPath(userID, datasetID) / SHARES_DIR_NAME

  private inline fun shareUserPath(userID: UserID, datasetID: DatasetID, shareUserID: UserID) =
    sharePath(userID, datasetID) / shareUserID.toString()

  // endregion Path "Constants"
}

