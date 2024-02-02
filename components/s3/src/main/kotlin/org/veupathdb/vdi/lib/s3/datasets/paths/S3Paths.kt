package org.veupathdb.vdi.lib.s3.datasets.paths

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectoryImpl
import org.veupathdb.vdi.lib.s3.datasets.util.PathBuilder

@Suppress("NOTHING_TO_INLINE")
object S3Paths {

  const val ROOT_DIR_NAME = "vdi"
  const val UPLOAD_DIR_NAME = "upload"
  const val DATASET_DIR_NAME = "dataset"
  const val DATA_DIR_NAME = "data"
  const val SHARES_DIR_NAME = "shares"

  const val DELETE_FLAG_FILE_NAME = "delete-flag"

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
   * `{root}/{user-id}/{dataset-id}/`
   */
  fun datasetDir(userID: UserID, datasetID: DatasetID) =
    (datasetPath(userID, datasetID) / "").toString()

  /**
   * `{root}/{user-id}/{dataset-id}/dataset/vdi-manifest.json`
   */
  fun datasetManifestFile(userID: UserID, datasetID: DatasetID) =
    (datasetContentPath(userID, datasetID) / DatasetManifestFilename).toString()

  /**
   * `{root}/{user-id}/{dataset-id}/dataset/vdi-meta.json`
   */
  fun datasetMetaFile(userID: UserID, datasetID: DatasetID) =
    (datasetContentPath(userID, datasetID) / DatasetMetaFilename).toString()

  /**
   * `{root}/{user-id}/{dataset-id}/dataset/delete-flag`
   */
  fun datasetDeleteFlagFile(userID: UserID, datasetID: DatasetID) =
    (datasetContentPath(userID, datasetID) / DELETE_FLAG_FILE_NAME).toString()

  /**
   * `{root}/{user-id}/{dataset-id}/dataset/data/`
   */
  fun datasetDataDir(userID: UserID, datasetID: DatasetID) =
    (dataPath(userID, datasetID) / "").toString()

  /**
   * `{root}/{user-id}/{dataset-id}/dataset/data/{filename}`
   */
  fun datasetDataFile(userID: UserID, datasetID: DatasetID, fileName: String) =
    (dataPath(userID, datasetID) / fileName).toString()

  /**
   * `{root}/{user-id}/{dataset-id}/dataset/shares/`
   */
  fun datasetSharesDir(userID: UserID, datasetID: DatasetID) =
    (sharePath(userID, datasetID) / "").toString()

  /**
   * `{root}/{user-id}/{dataset-id}/dataset/shares/{recipient-id}/offer.json`
   */
  fun datasetShareOfferFile(userID: UserID, datasetID: DatasetID, recipientID: UserID) =
    (shareUserPath(userID, datasetID, recipientID) / SHARE_OFFER_FILE_NAME).toString()

  /**
   * `{root}/{user-id}/{dataset-id}/dataset/shares/{recipient-id}/receipt.json`
   */
  fun datasetShareReceiptFile(userID: UserID, datasetID: DatasetID, recipientID: UserID) =
    (shareUserPath(userID, datasetID, recipientID) / SHARE_RECEIPT_FILE_NAME).toString()

  /**
   * `{root}/{user-id}/{dataset-id}/upload/`
   */
  fun datasetUploadsDir(userID: UserID, datasetID: DatasetID) =
    (uploadPath(userID, datasetID) / "").toString()

  /**
   * `{root}/{user-id}/{dataset-id}/upload/{filename}`
   */
  fun datasetUploadFile(userID: UserID, datasetID: DatasetID, fileName: String) =
    (uploadPath(userID, datasetID) / fileName).toString()

  fun identifyDatasetDir(path: String, bucket: S3Bucket): DatasetDirectory {
    val pathWithoutRoot = path.removePrefix(rootDir())
    val pathTokens = pathWithoutRoot.split("/")
    val userID = UserID(pathTokens[0])
    val datasetID = DatasetID(pathTokens[1])
    return DatasetDirectoryImpl(
      ownerID = userID,
      datasetID = datasetID,
      pathFactory = S3DatasetPathFactory(userID, datasetID),
      bucket = bucket
    )
  }

  // endregion Public API


  // region Path "Constants"

  /**
   * `{rootDir}`
   */
  private inline fun rootPath() =
    PathBuilder(ROOT_DIR_NAME)

  /**
   * `{rootDir}/{userID}`
   */
  private inline fun userPath(userID: UserID) =
    rootPath() / userID.toString()

  /**
   * `{rootDir}/{userID}/{datasetID}`
   */
  private inline fun datasetPath(userID: UserID, datasetID: DatasetID) =
    userPath(userID) / datasetID.toString()

  /**
   * `{rootDir}/{userID}/{datasetID}/dataset`
   */
  private inline fun datasetContentPath(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID) / DATASET_DIR_NAME

  /**
   * `{rootDir}/{userID}/{datasetID}/upload`
   */
  private inline fun uploadPath(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID) / UPLOAD_DIR_NAME

  /**
   * `{rootDir}/{userID}/{datasetID}/dataset/data`
   */
  private inline fun dataPath(userID: UserID, datasetID: DatasetID) =
    datasetContentPath(userID, datasetID) / DATA_DIR_NAME

  /**
   * `{rootDir}/{userID}/{datasetID}/dataset/shares`
   */
  private inline fun sharePath(userID: UserID, datasetID: DatasetID) =
    datasetContentPath(userID, datasetID) / SHARES_DIR_NAME

  /**
   * `{rootDir}/{userID}/{datasetID}/dataset/shares/{recipientID}`
   */
  private inline fun shareUserPath(userID: UserID, datasetID: DatasetID, shareUserID: UserID) =
    sharePath(userID, datasetID) / shareUserID.toString()

  // endregion Path "Constants"
}

