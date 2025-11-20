package vdi.core.s3

import java.time.OffsetDateTime


/**
 * Returns the most recent file last modified timestamp out of all the dataset
 * share offer or receipt files that appear in MinIO.
 */
fun DatasetDirectory.getLatestShareTimestamp(fallback: OffsetDateTime): OffsetDateTime {
  var latest = fallback

  getShares().values.forEach { (offer, receipt) ->
    offer.lastModified()?.also { if (it.isAfter(latest)) latest = it }
    receipt.lastModified()?.also { if (it.isAfter(latest)) latest = it }
  }

  return latest
}


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
fun DatasetDirectory.getMetaTimestamp() = getMetaFile().lastModified()

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
fun DatasetDirectory.getManifestTimestamp() = getManifestFile().lastModified()

/**
 * Returns the last modified timestamp of the raw user upload file in this
 * dataset directory.
 *
 * If this dataset directory does not contain a raw user upload file at the time
 * of this method call, this method will return `null`.
 *
 * @return The last modified timestamp of the raw user upload file if it exists,
 * otherwise `null`.
 */
fun DatasetDirectory.getUploadTimestamp() = getUploadFile().lastModified()

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
fun DatasetDirectory.getImportReadyTimestamp() = getImportReadyFile().lastModified()

/**
 * Returns the last modified timestamp of the install-ready data file in this
 * dataset directory.
 *
 * If this dataset directory does not contain an install-ready file at the time
 * of this method call, this method will return `null`.
 *
 * @return The last modified timestamp of the install-ready data file if it
 * exists, otherwise `null`.
 */
fun DatasetDirectory.getInstallReadyTimestamp() = getInstallReadyFile().lastModified()

/**
 * Returns the last modified timestamp of the delete-flag file in this dataset
 * directory.
 *
 * If this dataset directory does not contain an delete-flag file at the time of
 * this method call, this method will return `null`.
 *
 * @return The last modified timestamp of the delete-flag file if it exists,
 * otherwise `null`.
 */
fun DatasetDirectory.getDeleteFlagTimestamp() = getDeleteFlag().lastModified()

fun DatasetDirectory.getLatestMappingTimestamp() =
  getMappingFiles()
    .mapNotNull { it.lastModified() }
    .maxOrNull()