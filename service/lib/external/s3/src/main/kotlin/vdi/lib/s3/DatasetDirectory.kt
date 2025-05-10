package vdi.lib.s3

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareOffer
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareReceipt
import java.io.InputStream
import java.time.OffsetDateTime
import vdi.lib.s3.files.*
import vdi.lib.s3.paths.S3File

interface DatasetDirectory {

  val ownerID: UserID

  val datasetID: DatasetID

  /**
   * Tests whether this [DatasetDirectory] currently exists.
   *
   * As S3 does not have the concept of a "directory" itself, the existence of
   * this [DatasetDirectory] representation is determined by whether an object
   * already exists under this "directory's" path (prefix).
   *
   * This means that if this method returns false, then no contents have been
   * uploaded to S3 for this dataset yet.
   *
   * @return `true` if this [DatasetDirectory] exists (contains something),
   * otherwise `false`.
   */
  fun exists(): Boolean

  /**
   * Tests whether this [DatasetDirectory] currently contains a metadata JSON
   * file.
   *
   * @return `true` if this dataset contains or, at the time of this method
   * call, contained a metadata JSON file.
   */
  fun hasMetaFile(): Boolean

  /**
   * Returns a representation of this [DatasetDirectory]'s metadata JSON file.
   *
   * This method will return a value regardless of whether the metadata JSON
   * file exists or existed.  The existence of the file can be tested using the
   * returned object's [DatasetMetaFile.exists] method.
   */
  fun getMetaFile(): DatasetMetaFile

  /**
   * Puts a metadata JSON file into this [DatasetDirectory] overwriting any
   * previously existing metadata JSON file.
   *
   * @param meta Dataset metadata to write to the metadata JSON file.
   */
  fun putMetaFile(meta: VDIDatasetMeta)

  /**
   * Deletes the metadata JSON file from this [DatasetDirectory].
   */
  fun deleteMetaFile()

  /**
   * Tests whether this [DatasetDirectory] currently contains a manifest JSON
   * file.
   *
   * @return `true` if this dataset contains, or at the time of this method
   * call, contained a manifest JSON file.
   */
  fun hasManifestFile(): Boolean

  /**
   * Returns a representation of this [DatasetDirectory]'s manifest JSON file.
   *
   * This method will return a value regardless of whether the manifest JSON
   * file exists or existed.  The existence of the file can be tested using the
   * returned object's [DatasetManifestFile.exists] method.
   */
  fun getManifestFile(): DatasetManifestFile

  /**
   * Puts a manifest JSON file into this [DatasetDirectory] overwriting any
   * previously existing manifest JSON file.
   *
   * @param manifest Dataset manifest to write to the manifest JSON file.
   */
  fun putManifestFile(manifest: VDIDatasetManifest)

  /**
   * Deletes the manifest JSON file from this [DatasetDirectory].
   */
  fun deleteManifestFile()

  /**
   * Tests whether this [DatasetDirectory] currently contains the
   * [delete flag][S3File.DeleteFlag] file.
   *
   * @return `true` if this dataset contains, or at the time of this method
   * call, contained a delete flag file.
   */
  fun hasDeleteFlag(): Boolean

  /**
   * Returns a representation of this [DatasetDirectory]'s
   * [delete flag][S3File.DeleteFlag] file.
   *
   * This method will return a value regardless of whether the delete flag
   * file exists or existed.  The existence of the file can be tested using the
   * returned object's [DatasetDeleteFlagFile.exists] method.
   */
  fun getDeleteFlag(): DatasetDeleteFlagFile

  /**
   * Puts a [delete flag][S3File.DeleteFlag] file into this
   * [DatasetDirectory].
   *
   * If the file already exists, it will be overwritten with a new flag file
   * with a new modified timestamp.
   */
  fun putDeleteFlag()

  /**
   * Deletes the [delete flag][S3File.DeleteFlag] file from this
   * [DatasetDirectory].
   */
  fun deleteDeleteFlag()

  /**
   * Tests whether this [DatasetDirectory] currently contains the
   * [revised dataset flag][S3File.RevisionFlag] file.
   *
   * @return `true` if this dataset contains, or at the time of this method
   * call, contained a revised dataset flag file.
   */
  fun hasRevisedFlag(): Boolean

  fun getRevisedFlag(): DatasetRevisionFlagFile

  fun putRevisedFlag()

  fun deleteRevisedFlag()

  /**
   * Tests whether this [DatasetDirectory] currently contains a `raw-upload.zip`
   * file.
   *
   * @return `true` if this dataset contains, or at the time of this method
   * call, contained a `raw-upload.zip` file.
   */
  fun hasUploadFile(): Boolean

  /**
   * Returns a representation of this [DatasetDirectory]'s `raw-upload.zip`
   * file.
   *
   * This method will return a value regardless of whether the `raw-upload.zip`
   * file exists or existed.  The existence of the file can be tested using the
   * returned object's [DatasetDeleteFlagFile.exists] method.
   */
  fun getUploadFile(): DatasetRawUploadFile

  /**
   * Puts a `raw-upload.zip` file into this [DatasetDirectory].
   */
  fun putUploadFile(fn: () -> InputStream)

  /**
   * Deletes the `raw-upload.zip` file from this [DatasetDirectory].
   */
  fun deleteUploadFile()

  /**
   * Tests whether this [DatasetDirectory] currently contains an
   * `import-ready.zip` file.
   *
   * @return `true` if this dataset contains, or at the time of this method
   * call, contained a `import-ready.zip` file.
   */
  fun hasImportReadyFile(): Boolean

  /**
   * Returns a representation of this [DatasetDirectory]'s `import-ready.zip`
   * file.
   *
   * This method will return a value regardless of whether the
   * `import-ready.zip` file exists or existed.  The existence of the file can
   * be tested using the returned object's [DatasetDeleteFlagFile.exists]
   * method.
   */
  fun getImportReadyFile(): DatasetImportableFile

  /**
   * Puts an `import-ready.zip` file into this [DatasetDirectory].
   */
  fun putImportReadyFile(fn: () -> InputStream)

  /**
   * Deletes the `import-ready.zip` file from this [DatasetDirectory].
   */
  fun deleteImportReadyFile()

  /**
   * Tests whether this [DatasetDirectory] currently contains an
   * `install-ready.zip` file.
   *
   * @return `true` if this dataset contains, or at the time of this method
   * call, contained a `install-ready.zip` file.
   */
  fun hasInstallReadyFile(): Boolean

  /**
   * Returns a representation of this [DatasetDirectory]'s `install-ready.zip`
   * file.
   *
   * This method will return a value regardless of whether the
   * `install-ready.zip` file exists or existed.  The existence of the file can
   * be tested using the returned object's [DatasetDeleteFlagFile.exists]
   * method.
   */
  fun getInstallReadyFile(): DatasetInstallableFile

  /**
   * Puts an `install-ready.zip` file into this [DatasetDirectory].
   */
  fun putInstallReadyFile(fn: () -> InputStream)

  /**
   * Deletes the `install-ready.zip` file from this [DatasetDirectory].
   */
  fun deleteInstallReadyFile()

  /**
   * Fetches a map of [DatasetShare]s from this [DatasetDirectory].
   *
   * The returned map will be keyed on the ID of the share recipient.
   *
   * The values in the returned map provide access to further information about
   * the share itself.
   */
  fun getShares(): Map<UserID, DatasetShare>

  /**
   * Puts a new "share" into this [DatasetDirectory] by creating a share offer
   * and share receipt file in the `DatasetDirectory` with default values that
   * automatically accept the share on behalf of the recipient..
   *
   * @param recipientID ID of the share recipient.
   */
  fun putShare(recipientID: UserID)

  /**
   * Puts a new "share" into this [DatasetDirectory] by creating a share offer
   * and share receipt file in the `DatasetDirectory`.
   *
   * @param recipientID ID of the share recipient.
   *
   * @param offer Share offer.
   *
   * @param receipt Share receipt.
   */
  fun putShare(recipientID: UserID, offer: VDIDatasetShareOffer, receipt: VDIDatasetShareReceipt)

  /**
   * Returns the last modified timestamp of the dataset metadata file in this
   * dataset directory.
   *
   * If this dataset directory does not contain a metadata file at the time of
   * this method call, this method will return `null`.
   *
   * @return The last modified timestamp of the dataset metadata file if it
   * exists, otherwise `null`.
   */
  fun getMetaTimestamp() = getMetaFile().lastModified()

  /**
   * Returns the last modified timestamp of the dataset manifest file in this
   * dataset directory.
   *
   * If this dataset directory does not contain a manifest file at the time of
   * this method call, this method will return `null`.
   *
   * @return The last modified timestamp of the dataset manifest file if it
   * exists, otherwise `null`.
   */
  fun getManifestTimestamp() = getManifestFile().lastModified()

  /**
   * Returns the last modified timestamp of the raw user upload file in this
   * dataset directory.
   *
   * If this dataset directory does not contain a raw user upload file at the
   * time of this method call, this method will return `null`.
   *
   * @return The last modified timestamp of the raw user upload file if it
   * exists, otherwise `null`.
   */
  fun getUploadTimestamp() = getUploadFile().lastModified()

  /**
   * Returns the last modified timestamp of the import-ready data file in this
   * dataset directory.
   *
   * If this dataset directory does not contain an import-ready file at the time
   * of this method call, this method will return `null`.
   *
   * @return The last modified timestamp of the import-ready data file if it
   * exists, otherwise `null`.
   */
  fun getImportReadyTimestamp() = getImportReadyFile().lastModified()

  /**
   * Returns the last modified timestamp of the install-ready data file in this
   * dataset directory.
   *
   * If this dataset directory does not contain an install-ready file at the
   * time of this method call, this method will return `null`.
   *
   * @return The last modified timestamp of the install-ready data file if it
   * exists, otherwise `null`.
   */
  fun getInstallReadyTimestamp() = getInstallReadyFile().lastModified()

  /**
   * Returns the last modified timestamp of the delete-flag file in this dataset
   * directory.
   *
   * If this dataset directory does not contain an delete-flag file at the time
   * of this method call, this method will return `null`.
   *
   * @return The last modified timestamp of the delete-flag file if it
   * exists, otherwise `null`.
   */
  fun getDeleteFlagTimestamp() = getDeleteFlag().lastModified()

  /**
   * Returns the most recent file last modified timestamp out of all the dataset
   * share offer or receipt files that appear in MinIO.
   */
  fun getLatestShareTimestamp(fallback: OffsetDateTime): OffsetDateTime {
    var latest = fallback

    getShares().forEach { (_, share) ->
      share.offer.lastModified()?.also { if (it.isAfter(latest)) latest = it }
      share.receipt.lastModified()?.also { if (it.isAfter(latest)) latest = it }
    }

    return latest
  }
}
