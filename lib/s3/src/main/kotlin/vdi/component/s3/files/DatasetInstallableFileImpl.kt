package vdi.component.s3.files

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.component.s3.paths.S3Paths
import java.io.InputStream
import java.time.OffsetDateTime

internal class DatasetInstallableFileImpl(
  path: String,
  existsChecker: () -> Boolean = { false },
  lastModifiedSupplier: () -> OffsetDateTime? = { null },
  loadObjectStream: () -> InputStream? = { null }
)
  : DatasetFileImpl(path, existsChecker, lastModifiedSupplier, loadObjectStream)
  , DatasetInstallableFile
{
  constructor(
    bucket: S3Bucket,
    path: String,
  ): this(
    path = path,
    // This looks weird, but we use list instead of stat since stat only returns seconds resolution, not milliseconds.
    lastModifiedSupplier = { bucket.objects.list(path).stream().findFirst().map { o -> o.lastModified }.orElse(null) },
    existsChecker = { path in bucket.objects },
    loadObjectStream = { bucket.objects.open(path)?.stream }
  )

  constructor(s3Object: S3Object) : this(
    path = s3Object.path,
    lastModifiedSupplier = s3Object::lastModified,
    existsChecker = { true }, // It definitely exists if loaded from an actual S3 object
    loadObjectStream = { s3Object.bucket.objects.open(s3Object.path)?.stream }
  ) {
    if (s3Object.baseName != S3Paths.InstallReadyZipName) {
      throw IllegalArgumentException("Can only construct an install-ready file from s3 object if object base name is "
        + S3Paths.InstallReadyZipName + ". Given path: " + s3Object.path)
    }
  }
}