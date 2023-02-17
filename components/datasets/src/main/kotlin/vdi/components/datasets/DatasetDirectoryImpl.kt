package vdi.components.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.components.common.DatasetID

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

  override fun listDataFiles(): List<DatasetFileHandle> {
    return bucket.objects
      .list(S3Path.dataDir(ownerID, datasetID))
      .map { DatasetFileHandleImpl(bucket, it) }
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

