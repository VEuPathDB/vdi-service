package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

enum class S3File(val baseName: String) {
  /**
   * Name of the flag object used to indicate that a dataset has soft-deleted.
   */
  DeleteFlag("delete-flag"),

  /**
   * Name of the dataset upload archive that has been uploaded, safety checked,
   * repacked, and is available to be run through import processing.
   */
  ImportReadyZip("import-ready.zip"),

  /**
   * Name of the import-processed dataset archive that has been validated and
   * transformed, and is available to be installed into target applications.
   */
  InstallReadyZip("install-ready.zip"),

  Metadata(DatasetMetaFilename),

  Manifest(DatasetManifestFilename),

  /**
   * Name of the raw user upload file that is awaiting safety checking and
   * repacking into an import ready archive.
   */
  RawUploadZip("raw-upload.zip"),

  /**
   * Name of the flag object used to indicate that a dataset has been revised.
   */
  RevisionFlag("revised-flag"),

  SharesDir("shares") {
    private val pat = Regex("\\bshares\\b")
    override fun resembles(path: String) = path.contains(pat)
  },

  ShareOffer("share-offer.json"),

  ShareReceipt("share-receipt.json"),
  ;

  override fun toString() = when (this) {
    SharesDir -> "$baseName/"
    else      -> baseName
  }

  companion object {
    fun fromStringOrNull(name: String) =
      entries.firstOrNull { it.baseName == name }

    @Suppress("NOTHING_TO_INLINE")
    inline fun String.resembles(target: S3File) =
      target.resembles(this)

    @OptIn(ExperimentalContracts::class)
    inline fun String.resembles(target: S3File, fn: () -> Unit): Boolean {
      contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
      return target.resembles(this).also { if (it) fn() }
    }
  }

  open fun resembles(path: String) = path.endsWith(baseName)
}

