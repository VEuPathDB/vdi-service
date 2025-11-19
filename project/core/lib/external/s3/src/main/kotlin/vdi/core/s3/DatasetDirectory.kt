package vdi.core.s3

import java.io.InputStream
import vdi.core.s3.files.meta.MetadataFile
import vdi.core.s3.files.DatasetFile
import vdi.core.s3.files.data.DataFile
import vdi.core.s3.files.docs.DocumentFile
import vdi.core.s3.files.flags.FlagFile
import vdi.core.s3.files.maps.MappingFile
import vdi.core.s3.files.meta.ManifestFile
import vdi.core.s3.files.shares.ShareFile
import vdi.core.s3.files.shares.ShareOffer
import vdi.core.s3.files.shares.ShareReceipt
import vdi.model.meta.*

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

  // region vdi-meta.json

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
   * returned object's [DatasetFile.exists] method.
   */
  fun getMetaFile(): MetadataFile

  /**
   * Puts a metadata JSON file into this [DatasetDirectory] overwriting any
   * previously existing metadata JSON file.
   *
   * @param meta Dataset metadata to write to the metadata JSON file.
   */
  fun putMetaFile(meta: DatasetMetadata)

  /**
   * Deletes the metadata JSON file from this [DatasetDirectory].
   */
  fun deleteMetaFile()

  // endregion vdi-meta.json

  // region vdi-manifest.json

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
   * returned object's [DatasetFile.exists] method.
   */
  fun getManifestFile(): ManifestFile

  /**
   * Puts a manifest JSON file into this [DatasetDirectory] overwriting any
   * previously existing manifest JSON file.
   *
   * @param manifest Dataset manifest to write to the manifest JSON file.
   */
  fun putManifestFile(manifest: DatasetManifest)

  /**
   * Deletes the manifest JSON file from this [DatasetDirectory].
   */
  fun deleteManifestFile()

  // endregion vdi-manifest.json

  // region delete flag

  /**
   * Tests whether this [DatasetDirectory] currently contains a soft-delete
   * marker file.
   *
   * @return `true` if this dataset contains, or at the time of this method
   * call, contained a delete flag file.
   */
  fun hasDeleteFlag(): Boolean

  /**
   * Returns a representation of this [DatasetDirectory]'s soft-delete marker
   * file.
   *
   * This method will return a value regardless of whether the delete flag file
   * exists or existed.  The existence of the file can be tested using the
   * returned object's [DatasetFile.exists] method.
   */
  fun getDeleteFlag(): FlagFile

  /**
   * Puts a soft-delete marker file into this [DatasetDirectory].
   *
   * If the file already exists, it will be overwritten with a new flag file
   * with a new modified timestamp.
   */
  fun putDeleteFlag()

  /**
   * Deletes the soft-delete marker file from this [DatasetDirectory].
   */
  fun deleteDeleteFlag()

  // endregion delete flag

  // region revised flag

  /**
   * Tests whether this [DatasetDirectory] currently contains the obsolete or
   * revised data marker file.
   *
   * @return `true` if this dataset contains, or at the time of this method
   * call, contained a revised dataset flag file.
   */
  fun hasRevisedFlag(): Boolean

  fun getRevisedFlag(): FlagFile

  fun putRevisedFlag()

  fun deleteRevisedFlag()

  // endregion delete flag

  // region raw upload

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
   * returned object's [DatasetFile.exists] method.
   */
  fun getUploadFile(): DataFile

  /**
   * Puts a `raw-upload.zip` file into this [DatasetDirectory].
   */
  fun putUploadFile(fn: () -> InputStream)

  /**
   * Deletes the `raw-upload.zip` file from this [DatasetDirectory].
   */
  fun deleteUploadFile()

  // endregion raw upload

  // region import-ready

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
   * be tested using the returned object's [DatasetFile.exists]
   * method.
   */
  fun getImportReadyFile(): DataFile

  /**
   * Puts an `import-ready.zip` file into this [DatasetDirectory].
   */
  fun putImportReadyFile(fn: () -> InputStream)

  /**
   * Deletes the `import-ready.zip` file from this [DatasetDirectory].
   */
  fun deleteImportReadyFile()

  // endregion import-ready

  // region install-ready

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
   * be tested using the returned object's [DatasetFile.exists] method.
   */
  fun getInstallReadyFile(): DataFile

  /**
   * Puts an `install-ready.zip` file into this [DatasetDirectory].
   */
  fun putInstallReadyFile(fn: () -> InputStream)

  /**
   * Deletes the `install-ready.zip` file from this [DatasetDirectory].
   */
  fun deleteInstallReadyFile()

  // endregion install-ready

  // region shares

  /**
   * Fetches a map of [ShareFile]s from this [DatasetDirectory].
   *
   * The returned map will be keyed on the ID of the share recipient.
   *
   * The values in the returned map provide access to further information about
   * the share itself.
   */
  fun getShares(): Map<UserID, Pair<ShareOffer, ShareReceipt>>

  /**
   * Puts a new "share" into this [DatasetDirectory] by creating a share offer
   * and share receipt file in the `DatasetDirectory` with default values that
   * automatically accept the share on behalf of the recipient.
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
  fun putShare(recipientID: UserID, offer: DatasetShareOffer, receipt: DatasetShareReceipt)

  // endregion shares

  // region mapping files

  fun getMappingFiles(): Sequence<MappingFile>

  fun getMappingFile(name: String): MappingFile?

  fun putMappingFile(name: String, fn: () -> InputStream)

  fun deleteMappingFile(name: String)

  // endregion mapping files

  // region document files

  fun getDocumentFiles(): Sequence<DocumentFile>

  fun getDocumentFile(name: String): DocumentFile?

  fun putDocumentFile(name: String, fn: () -> InputStream)

  fun deleteDocumentFile(name: String)

  // endregion document files
}
