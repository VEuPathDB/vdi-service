package vdi.lib.s3

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket

fun DatasetObjectStore(s3Bucket: S3Bucket): DatasetObjectStore =
  DatasetObjectStoreImpl(s3Bucket)
