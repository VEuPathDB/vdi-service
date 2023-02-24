package vdi.components.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket

internal class DatasetDeleteFlagFileImpl(bucket: S3Bucket, path: String)
: DatasetFileImpl(bucket, path)
, DatasetDeleteFlagFile