package vdi.components.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket

internal class DatasetDataFileImpl(bucket: S3Bucket, path: String) : DatasetFileImpl(bucket, path), DatasetDataFile {
  override fun open() = bucket.objects.open(path)?.stream
}
