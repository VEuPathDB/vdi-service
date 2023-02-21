package vdi.components.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object

internal sealed class DatasetFileImpl(
  protected val bucket: S3Bucket,
  protected val path: String,
) : DatasetFile {
  override fun exists() = path in bucket.objects
  override fun lastModified() = bucket.objects.stat(path)?.lastModified
}