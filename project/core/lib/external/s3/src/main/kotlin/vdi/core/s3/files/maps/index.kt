package vdi.core.s3.files.maps

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object

fun MappingFile(path: String, bucket: ObjectContainer): DataPropertiesFile =
  LazyLoadDataProperties(path, bucket)

fun MappingFile(s3Object: S3Object): DataPropertiesFile =
  MaterializedDataProperties(s3Object)