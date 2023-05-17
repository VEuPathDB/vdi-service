package org.veupathdb.vdi.lib.s3.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket

internal class DatasetUploadFileImpl(bucket: S3Bucket, path: String)
  : DatasetFileImpl(path.substring(path.lastIndexOf('/') + 1), bucket, path)
  , DatasetUploadFile
{
  override fun open() = loadContents()
}