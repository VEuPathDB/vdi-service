package vdi.core.s3.files.meta

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object

fun ManifestFile(path: String, bucket: ObjectContainer): ManifestFile =
  LazyLoadManifest(path, bucket)

fun ManifestFile(s3Object: S3Object): ManifestFile =
  MaterializedManifest(s3Object)

fun MetaFile(path: String, bucket: ObjectContainer): MetadataFile =
  LazyLoadMetaFile(path, bucket)

fun MetaFile(s3Object: S3Object): MetadataFile =
  MaterializedMetaFile(s3Object)
