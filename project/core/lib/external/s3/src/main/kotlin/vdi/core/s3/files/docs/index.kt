package vdi.core.s3.files.docs

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object

fun DocumentFile(path: String, bucket: ObjectContainer): DocumentFile =
  LazyLoadDocument(path, bucket)

fun DocumentFile(s3Object: S3Object): DocumentFile =
  MaterializedDocument(s3Object)