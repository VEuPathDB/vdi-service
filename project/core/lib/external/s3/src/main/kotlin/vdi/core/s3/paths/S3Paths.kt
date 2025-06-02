package vdi.core.s3.paths

import vdi.core.s3.files.FileName
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.core.s3.util.PathBuilder
import vdi.model.DatasetManifestFilename
import vdi.model.DatasetMetaFilename


/**
 * S3 Dataset Path Creation Methods
 *
 * S3 "directory" structure:
 * ```
 * {user-id}/
 *   |- {dataset-id}/
 *        |- shares/
 *        |    |- {recipient-id}/
 *        |         |- offer.json
 *        |         |- receipt.json
 *        |- delete-flag
 *        |- import-ready.zip
 *        |- install-ready.zip
 *        |- raw-upload.zip
 *        |- revised-flag
 *        |- vdi-manifest.json
 *        |- vdi-meta.json
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
object S3Paths {

  // region Public API

  /**
   * `{user-id}/`
   */
  fun userDir(userID: UserID) =
    userPath(userID).dirPath()

  /**
   * `{user-id}/{dataset-id}/`
   */
  fun datasetDir(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).dirPath()

  /**
   * `{user-id}/{dataset-id}/vdi-manifest.json`
   */
  fun datasetManifestFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(FileName.ManifestFile).toString()

  /**
   * `{user-id}/{dataset-id}/vdi-meta.json`
   */
  fun datasetMetaFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(FileName.MetadataFile).toString()

  /**
   * `{user-id}/{dataset-id}/delete-flag`
   */
  fun datasetDeleteFlagFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(FileName.DeleteFlagFile).toString()

  /**
   * `{user-id}/{dataset-id}/revised-flag`
   */
  fun datasetRevisedFlagFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(FileName.RevisedFlagFile).toString()

  /**
   * `{user-id}/{dataset-id}/import-ready.zip`
   */
  fun datasetImportReadyFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(FileName.ImportReadyFile).toString()

  /**
   * `{user-id}/{dataset-id}/install-ready.zip`
   */
  fun datasetInstallReadyFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(FileName.InstallReadyFile).toString()

  fun datasetRawUploadFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(FileName.RawUploadFile).toString()

  /**
   * `{user-id}/{dataset-id}/shares/`
   */
  fun datasetSharesDir(userID: UserID, datasetID: DatasetID) =
    sharePath(userID, datasetID).dirPath()

  /**
   * `{user-id}/{dataset-id}/shares/{recipient-id}/offer.json`
   */
  fun datasetShareOfferFile(userID: UserID, datasetID: DatasetID, recipientID: UserID) =
    shareUserPath(userID, datasetID, recipientID).append(FileName.ShareOfferFile).toString()

  /**
   * `{user-id}/{dataset-id}/shares/{recipient-id}/receipt.json`
   */
  fun datasetShareReceiptFile(userID: UserID, datasetID: DatasetID, recipientID: UserID) =
    shareUserPath(userID, datasetID, recipientID).append(FileName.ShareReceiptFile).toString()

  /**
   * `{user-id}/{dataset-id}/documents/`
   */
  fun datasetDocumentsDir(userID: UserID, datasetID: DatasetID) =
    documentsPath(userID, datasetID).dirPath()

  /**
   * `{user-id}/{dataset-id}/documents/{file-name}`
   */
  fun datasetDocumentFile(userID: UserID, datasetID: DatasetID, fileName: String) =
    documentsPath(userID, datasetID).append(fileName).toString()

  // endregion Public API


  // region Path "Constants"

  /**
   * `{userID}/`
   */
  private inline fun userPath(userID: UserID) =
    PathBuilder(userID.toString())

  /**
   * `{userID}/{datasetID}/`
   */
  private inline fun datasetPath(userID: UserID, datasetID: DatasetID) =
    userPath(userID).append(datasetID.toString())

  /**
   * `{userID}/{datasetID}/shares/`
   */
  private inline fun sharePath(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(FileName.ShareDirectoryName)

  /**
   * `{userID}/{datasetID}/shares/{recipientID}/`
   */
  private inline fun shareUserPath(userID: UserID, datasetID: DatasetID, shareUserID: UserID) =
    sharePath(userID, datasetID).append(shareUserID.toString())

  /**
   * `{userID}/{datasetID}/documents/`
   */
  private inline fun documentsPath(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(FileName.DocumentDirectoryName)

  // endregion Path "Constants"
}
