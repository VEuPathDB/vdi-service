package org.veupathdb.vdi.lib.s3.datasets.files

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import java.io.InputStream
import java.time.OffsetDateTime

internal class DatasetInstallableFileImpl(
  path: String,
  private val existsChecker: () -> Boolean = { false },
  private val lastModifiedSupplier: () -> OffsetDateTime? = { null },
  private val loadObjectStream: () -> InputStream? = { null }
)
  : DatasetFileImpl(S3Paths.InstallReadyZipName, path, existsChecker, lastModifiedSupplier, loadObjectStream)
  , DatasetInstallableFile
{
  override fun open() = loadObjectStream()

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
  )
}