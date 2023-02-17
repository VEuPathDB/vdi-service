package vdi.components.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object

internal class DatasetFileHandleImpl(
  private val bucket: S3Bucket,
  private val handle: S3Object,
) : DatasetFileHandle {
  override fun openStream() = bucket.objects.open(handle.path)!!.stream
}