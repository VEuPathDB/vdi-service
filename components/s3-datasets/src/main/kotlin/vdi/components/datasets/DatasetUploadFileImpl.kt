package vdi.components.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket

internal class DatasetUploadFileImpl(bucket: S3Bucket, path: String) : DatasetFileImpl(bucket, path), DatasetUploadFile {
  override fun open() = bucket.objects.open(path)?.stream
}