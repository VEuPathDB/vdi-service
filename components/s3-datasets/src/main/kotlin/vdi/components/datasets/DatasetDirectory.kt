package vdi.components.datasets

import java.io.InputStream
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID
import vdi.components.datasets.model.DatasetManifest
import vdi.components.datasets.model.DatasetMeta

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
   * Tests whether this [DatasetDirectory] currently contains a `meta.json`
   * file.
   *
   * @return `true` if this dataset contains or, at the time of this method
   * call, contained a `meta.json` file.
   */
  fun hasMeta(): Boolean

  /**
   * Returns a representation of this [DatasetDirectory]'s `meta.json` file.
   *
   * This method will return a value regardless of whether the `meta.json` file
   * exists or existed.  The existence of the file can be tested using the
   * returned object's [DatasetMetaFile.exists] method.
   */
  fun getMeta(): DatasetMetaFile

  /**
   * Puts a `meta.json` file into this [DatasetDirectory] overwriting any
   * previously existing `meta.json` file.
   *
   * @param meta Dataset metadata to write to the `meta.json` file.
   */
  fun putMeta(meta: DatasetMeta)

  /**
   * Tests whether this [DatasetDirectory] currently contains a `manifest.json`
   * file.
   *
   * @return `true` if this dataset contains, or at the time of this method
   * call, contained a `manifest.json` file.
   */
  fun hasManifest(): Boolean

  /**
   * Returns a representation of this [DatasetDirectory]'s `manifest.json` file.
   *
   * This method will return a value regardless of whether the `manifest.json`
   * file exists or existed.  The existence of the file can be tested using the
   * returned object's [DatasetManifestFile.exists] method.
   */
  fun getManifest(): DatasetManifestFile

  /**
   * Puts a `manifest.json` file into this [DatasetDirectory] overwriting any
   * previously existing `manifest.json` file.
   *
   * @param manifest Dataset manifest to write to the `manifest.json` file.
   */
  fun putManifest(manifest: DatasetManifest)

  /**
   * Tests whether this [DatasetDirectory] currently contains a `delete-flag`
   * file.
   *
   * @return `true` if this dataset contains, or at the time of this method
   * call, contained a `delete-flag` file.
   */
  fun hasDeleteFlag(): Boolean

  /**
   * Returns a representation of this [DatasetDirectory]'s `delete-flag` file.
   *
   * This method will return a value regardless of whether the `delete-flag`
   * file exists or existed.  The existence of the file can be tested using the
   * returned object's [DatasetDeleteFlagFile.exists] method.
   */
  fun getDeleteFlag(): DatasetDeleteFlagFile

  /**
   * Puts a `delete-flag` file into this [DatasetDirectory].
   */
  fun putDeleteFlag()

  /**
   * Fetches a list of files under this [DatasetDirectory]'s upload file
   * path/key prefix.
   *
   * If no upload files have been put into this [DatasetDirectory], the returned
   * list will be empty.
   */
  fun getUploadFiles(): List<DatasetUploadFile>

  /**
   * Puts a file under this [DatasetDirectory]'s upload file path/key prefix,
   * overwriting any previously existing object with the given [name].
   *
   * @param name Name of the file to upload.
   *
   * @param fn Provider of an input stream over the contents of the file to
   * upload.
   */
  fun putUploadFile(name: String, fn: () -> InputStream)

  /**
   * Fetches a list of files under t his [DatasetDirectory]'s data file path/key
   * prefix.
   *
   * If no data files have been put into this [DatasetDirectory], the returned
   * list will be empty.
   */
  fun getDataFiles(): List<DatasetDataFile>

  /**
   * Puts a file under this [DatasetDirectory]'s data file path/key prefix,
   * overwriting any previously existing object with the given [name].
   *
   * @param name Name of the file to upload.
   *
   * @param fn Provider of an input stream over the contents of the file to
   * upload.
   */
  fun putDataFile(name: String, fn: () -> InputStream)

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
   * and share receipt file into the `DatasetDirectory`.
   *
   * @param recipientID ID of the share recipient.
   */
  fun putShare(recipientID: UserID)
}

