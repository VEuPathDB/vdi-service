package vdi.component.s3.files

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.component.s3.paths.S3Paths
import java.io.InputStream
import java.time.OffsetDateTime

internal class DatasetDeleteFlagFileImpl(
  path: String,
  existsChecker: () -> Boolean = { false },
  lastModifiedSupplier: () -> OffsetDateTime? = { null },
  loadObjectStream: () -> InputStream? = { null },
  putObjectStream: (InputStream) -> Unit = { it.skip(Long.MAX_VALUE) }
)
  : DatasetFileImpl(path, existsChecker, lastModifiedSupplier, loadObjectStream, putObjectStream)
  , DatasetDeleteFlagFile
{

  constructor(
    bucket: S3Bucket,
    path: String,
  ) : this(
    path = path,
    // This looks weird, but we use list instead of stat since stat only returns seconds resolution, not milliseconds.
    lastModifiedSupplier = { bucket.objects.list(path).stream().findFirst().map { o -> o.lastModified }.orElse(null) },
    existsChecker = { path in bucket.objects },
    loadObjectStream = { bucket.objects.open(path)?.stream },
    putObjectStream = { bucket.objects.put(path, it) },
  )

  constructor(s3Object: S3Object) : this(
    path = s3Object.path,
    lastModifiedSupplier = s3Object::lastModified,
    existsChecker = { true }, // It definitely exists if loaded from an actual S3 object
    loadObjectStream = { s3Object.bucket.objects.open(s3Object.path)?.stream }
  ) {
    if (s3Object.baseName != S3Paths.DeleteFlagFileName) {
      throw IllegalArgumentException(
        "Can only construct a delete flag from s3 object if object base name is "
          + S3Paths.DeleteFlagFileName + ". Given path: " + s3Object.path
      )
    }
  }
}
