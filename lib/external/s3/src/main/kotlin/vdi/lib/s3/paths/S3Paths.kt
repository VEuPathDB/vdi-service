package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.s3.util.PathBuilder

/**
 * S3 Dataset Path Definitions
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

  const val SharesDirName = "shares"

  /**
   * Name of the flag object used to indicate that a dataset has soft-deleted.
   */
  const val DeleteFlagFileName = "delete-flag"

  /**
   * Name of the flag object used to indicate that a dataset has been revised.
   */
  const val RevisionFlagFileName = "revised-flag"

  /**
   * Name of the dataset upload archive that has been uploaded, safety checked,
   * repacked, and is available to be run through import processing.
   */
  const val ImportReadyZipName = "import-ready.zip"

  /**
   * Name of the import-processed dataset archive that has been validated and
   * transformed, and is available to be installed into target applications.
   */
  const val InstallReadyZipName = "install-ready.zip"

  /**
   * Name of the raw user upload file that is awaiting safety checking and
   * repacking into an import ready archive.
   */
  const val RawUploadZipName = "raw-upload.zip"

  inline val MetadataFileName get() = DatasetMetaFilename
  inline val ManifestFileName get() = DatasetManifestFilename

  const val ShareOfferFileName   = "share-offer.json"
  const val ShareReceiptFileName = "share-receipt.json"

  // region Public API

  /**
   * `{user-id}/`
   */
  fun userDir(userID: UserID) =
    (userPath(userID) + "").toString()

  /**
   * `{user-id}/{dataset-id}/`
   */
  fun datasetDir(userID: UserID, datasetID: DatasetID) =
    (datasetPath(userID, datasetID) + "").toString()

  /**
   * `{user-id}/{dataset-id}/vdi-manifest.json`
   */
  fun datasetManifestFile(userID: UserID, datasetID: DatasetID) =
    (datasetPath(userID, datasetID) + DatasetManifestFilename).toString()

  /**
   * `{user-id}/{dataset-id}/vdi-meta.json`
   */
  fun datasetMetaFile(userID: UserID, datasetID: DatasetID) =
    (datasetPath(userID, datasetID) + DatasetMetaFilename).toString()

  /**
   * `{user-id}/{dataset-id}/delete-flag`
   */
  fun datasetDeleteFlagFile(userID: UserID, datasetID: DatasetID) =
    (datasetPath(userID, datasetID) + DeleteFlagFileName).toString()

  /**
   * `{user-id}/{dataset-id}/delete-flag`
   */
  fun datasetRevisedFlagFile(userID: UserID, datasetID: DatasetID) =
    (datasetPath(userID, datasetID) + RevisionFlagFileName).toString()

  /**
   * `{user-id}/{dataset-id}/import-ready.zip`
   */
  fun datasetImportReadyFile(userID: UserID, datasetID: DatasetID) =
    (datasetPath(userID, datasetID) + ImportReadyZipName).toString()

  /**
   * `{user-id}/{dataset-id}/install-ready.zip`
   */
  fun datasetInstallReadyFile(userID: UserID, datasetID: DatasetID) =
    (datasetPath(userID, datasetID) + InstallReadyZipName).toString()

  fun datasetRawUploadFile(userID: UserID, datasetID: DatasetID) =
    (datasetPath(userID, datasetID) + RawUploadZipName).toString()

  /**
   * `{user-id}/{dataset-id}/shares/`
   */
  fun datasetSharesDir(userID: UserID, datasetID: DatasetID) =
    (sharePath(userID, datasetID) + "").toString()

  /**
   * `{user-id}/{dataset-id}/shares/{recipient-id}/offer.json`
   */
  fun datasetShareOfferFile(userID: UserID, datasetID: DatasetID, recipientID: UserID) =
    (shareUserPath(userID, datasetID, recipientID) + ShareOfferFileName).toString()

  /**
   * `{user-id}/{dataset-id}/shares/{recipient-id}/receipt.json`
   */
  fun datasetShareReceiptFile(userID: UserID, datasetID: DatasetID, recipientID: UserID) =
    (shareUserPath(userID, datasetID, recipientID) + ShareReceiptFileName).toString()

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
    userPath(userID) + datasetID.toString()

  /**
   * `{userID}/{datasetID}/shares/`
   */
  private inline fun sharePath(userID: UserID, datasetID: DatasetID) =
    datasetPath(userID, datasetID) + SharesDirName

  /**
   * `{userID}/{datasetID}/shares/{recipientID}/`
   */
  private inline fun shareUserPath(userID: UserID, datasetID: DatasetID, shareUserID: UserID) =
    sharePath(userID, datasetID) + shareUserID.toString()

  // endregion Path "Constants"
}

