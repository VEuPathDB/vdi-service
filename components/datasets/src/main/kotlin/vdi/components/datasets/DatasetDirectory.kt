package vdi.components.datasets

import java.io.InputStream
import java.time.OffsetDateTime
import vdi.components.datasets.model.DatasetManifest
import vdi.components.datasets.model.DatasetMeta

interface DatasetDirectory {

  /**
   * List the files in the Dataset's upload directory.
   */
  fun listUploadFiles(): List<DatasetFileHandle>

  /**
   * Put a file into the Dataset's upload directory.
   */
  fun putUploadFile(fileName: String, fn: () -> InputStream)

  /**
   * List the files in the Dataset's data files directory.
   */
  fun listDataFiles(): List<DatasetFileHandle>

  /**
   * Put a file into the Dataset's data files directory.
   */
  fun putDataFile(fileName: String, fn: () -> InputStream)

  /**
   * List existing shares.
   */
  fun listShares(): List<DatasetShare>

  /**
   * Put a new share.
   */
  fun putShare(recipientID: Long)

  /**
   * Whether this dataset directory currently contains a manifest file.
   */
  fun hasManifest(): Boolean

  /**
   * Loads the manifest file from this dataset directory.
   */
  fun getManifest(): DatasetManifest

  /**
   * Gets the last modified timestamp for the manifest file
   */
  fun getManifestTimestamp(): OffsetDateTime

  fun putManifest(manifest: DatasetManifest)

  fun hasMeta(): Boolean

  fun getMeta(): DatasetMeta

  fun getMetaTimestamp(): OffsetDateTime

  fun putMeta(meta: DatasetMeta)

  fun hasDeletedFlag(): Boolean

  fun putDeletedFlag()
}
