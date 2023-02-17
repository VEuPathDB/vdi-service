package vdi.components.datasets

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import java.io.InputStream
import vdi.components.common.DatasetID
import vdi.components.datasets.model.DatasetManifest
import vdi.components.datasets.model.DatasetMeta
import vdi.components.datasets.model.GrantObject
import vdi.components.datasets.model.ReceiptObject
import vdi.components.json.JSON

internal class DatasetDirectoryImpl(
  private val ownerID: Long,
  private val datasetID: DatasetID,
  private val bucket: S3Bucket
) : DatasetDirectory {

  override fun listUploadFiles(): List<DatasetFileHandle> {
    return bucket.objects
      .list(S3Path.uploadDir(ownerID, datasetID))
      .map { DatasetFileHandleImpl(bucket, it) }
  }

  override fun putUploadFile(fileName: String, fn: () -> InputStream) {
    fn().use { bucket.objects.put(S3Path.uploadFile(ownerID, datasetID, fileName), it) }
  }

  override fun listDataFiles(): List<DatasetFileHandle> {
    return bucket.objects
      .list(S3Path.dataDir(ownerID, datasetID))
      .map { DatasetFileHandleImpl(bucket, it) }
  }

  override fun putDataFile(fileName: String, fn: () -> InputStream) {
    fn().use { bucket.objects.put(S3Path.dataFile(ownerID, datasetID, fileName), it) }
  }

  override fun listShares(): List<DatasetShare> {
    val prefix = S3Path.shareDir(ownerID, datasetID)

    return bucket.objects.list(prefix)
      .stream()
      .map { parseRecipientID(prefix, it.path) }
      .distinct()
      .map { DatasetShareImpl(ownerID, datasetID, it, bucket) }
      .toList() as List<DatasetShare>
  }

  override fun putShare(recipientID: Long) {
    // Put the share grant
    bucket.objects.put(
      S3Path.shareOwnerStateFile(ownerID, datasetID, recipientID),
      JSON.writeValueAsBytes(GrantObject(DatasetShare.GrantState.Granted)).inputStream()
    )

    // Put the receipt accept
    bucket.objects.put(
      S3Path.shareRecipientStateFile(ownerID, datasetID, recipientID),
      JSON.writeValueAsBytes(ReceiptObject(DatasetShare.ReceiptState.Accepted)).inputStream()
    )
  }

  override fun hasMeta() = S3Path.metaFile(ownerID, datasetID) in bucket.objects

  override fun getMeta(): DatasetMeta =
    S3Path.metaFile(ownerID, datasetID)
      .let { bucket.objects.open(it) ?: throw IllegalStateException("meta file does not exist: $it") }
      .stream
      .use { JSON.readValue(it) }

  override fun putMeta(meta: DatasetMeta) {
    bucket.objects.put(S3Path.metaFile(ownerID, datasetID), JSON.writeValueAsBytes(meta).inputStream())
  }

  override fun hasManifest() = S3Path.manifestFile(ownerID, datasetID) in bucket.objects

  override fun getManifest(): DatasetManifest =
    S3Path.manifestFile(ownerID, datasetID)
      .let { bucket.objects.open(it) ?: throw IllegalStateException("manifest file does not exist: $it") }
      .stream
      .use { JSON.readValue(it) }

  override fun putManifest(manifest: DatasetManifest) {
    bucket.objects.put(S3Path.manifestFile(ownerID, datasetID), JSON.writeValueAsBytes(manifest).inputStream())
  }

  override fun hasDeletedFlag() = S3Path.deletedFlagFile(ownerID, datasetID) in bucket.objects

  override fun putDeletedFlag() {
    bucket.objects.touch(S3Path.deletedFlagFile(ownerID, datasetID))
  }

  private fun parseRecipientID(prefix: String, path: String): Long {
    val slash = path.indexOf('/', prefix.length)

    if (slash < 0)
      throw IllegalStateException("invalid share path: $path")

    val substr = path.substring(prefix.length, slash)

    return try {
      substr.toLong()
    } catch (e: Throwable) {
      throw IllegalStateException("invalid share path: $path", e)
    }
  }
}

