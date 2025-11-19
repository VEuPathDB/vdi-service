package vdi.core.s3.files.flags

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object

fun DeleteFlagFile(path: String, bucket: ObjectContainer): DeleteFlagFile =
  LazyLoadDeleteFlag(path, bucket)

fun DeleteFlagFile(s3Object: S3Object): DeleteFlagFile =
  MaterializedDeleteFlag(s3Object)

fun RevisedFlagFile(path: String, bucket: ObjectContainer): RevisedFlagFile =
  LazyLoadRevisedFlag(path, bucket)

fun RevisedFlagFile(s3Object: S3Object): RevisedFlagFile =
  MaterializedRevisedFlag(s3Object)