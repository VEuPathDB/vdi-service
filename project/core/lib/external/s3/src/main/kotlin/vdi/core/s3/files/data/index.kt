package vdi.core.s3.files.data

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object

fun ImportReadyFile(path: String, bucket: ObjectContainer): ImportReadyFile =
  LazyLoadImportZip(path, bucket)

fun ImportReadyFile(s3Object: S3Object): ImportReadyFile =
  MaterializedImportZip(s3Object)

fun InstallReadyFile(path: String, bucket: ObjectContainer): InstallReadyFile =
  LazyLoadInstallZip(path, bucket)

fun InstallReadyFile(s3Object: S3Object): InstallReadyFile =
  MaterializedInstallZip(s3Object)

fun RawUploadFile(path: String, bucket: ObjectContainer): RawUploadFile =
  LazyLoadRawUpload(path, bucket)

fun RawUploadFile(s3Object: S3Object): RawUploadFile =
  MaterializedRawUpload(s3Object)
