package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.lib.s3.paths.S3File.*
import vdi.lib.s3.util.PathBuilder


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
    datasetPath(userID, datasetID).append(DatasetManifestFilename).toString()

  /**
   * `{user-id}/{dataset-id}/vdi-meta.json`
   */
  fun datasetMetaFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(DatasetMetaFilename).toString()

  /**
   * `{user-id}/{dataset-id}/delete-flag`
   */
  fun datasetDeleteFlagFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(DeleteFlag.baseName).toString()

  /**
   * `{user-id}/{dataset-id}/delete-flag`
   */
  fun datasetRevisedFlagFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(RevisionFlag.baseName).toString()

  /**
   * `{user-id}/{dataset-id}/import-ready.zip`
   */
  fun datasetImportReadyFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(ImportReadyZip.baseName).toString()

  /**
   * `{user-id}/{dataset-id}/install-ready.zip`
   */
  fun datasetInstallReadyFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(InstallReadyZip.baseName).toString()

  fun datasetRawUploadFile(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID).append(RawUploadZip.baseName).toString()

  /**
   * `{user-id}/{dataset-id}/shares/`
   */
  fun datasetSharesDir(userID: UserID, datasetID: DatasetID) =
    sharePath(userID, datasetID).dirPath()

  /**
   * `{user-id}/{dataset-id}/shares/{recipient-id}/offer.json`
   */
  fun datasetShareOfferFile(userID: UserID, datasetID: DatasetID, recipientID: UserID) =
    shareUserPath(userID, datasetID, recipientID).append(ShareOffer.baseName).toString()

  /**
   * `{user-id}/{dataset-id}/shares/{recipient-id}/receipt.json`
   */
  fun datasetShareReceiptFile(userID: UserID, datasetID: DatasetID, recipientID: UserID) =
    shareUserPath(userID, datasetID, recipientID).append(ShareReceipt.baseName).toString()

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
    datasetPath(userID, datasetID).append(SharesDir.baseName)

  /**
   * `{userID}/{datasetID}/shares/{recipientID}/`
   */
  private inline fun shareUserPath(userID: UserID, datasetID: DatasetID, shareUserID: UserID) =
    sharePath(userID, datasetID).append(shareUserID.toString())

  // endregion Path "Constants"
}
