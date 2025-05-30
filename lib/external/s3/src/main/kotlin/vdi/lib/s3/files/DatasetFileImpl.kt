package vdi.lib.s3.files

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import java.io.InputStream
import java.time.OffsetDateTime

internal sealed class DatasetFileImpl(
  override val path: String,
  private val existsChecker: () -> Boolean,
  private val lastModifiedSupplier: () -> OffsetDateTime?,
  private val loadObjectStream: () -> InputStream?,
  private val putObjectStream: (InputStream) -> Unit,
) : DatasetFile {

  constructor(s3Object: S3Object) : this(
    path = s3Object.path,
    lastModifiedSupplier = s3Object::lastModified,
    existsChecker = { true }, // It definitely exists if loaded from an actual S3 object
    loadObjectStream = { s3Object.bucket.objects.open(s3Object.path)?.stream },
    putObjectStream = { s3Object.bucket.objects.put(s3Object.path, it) },
  )

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

  override fun exists() = existsChecker()
  override fun lastModified(): OffsetDateTime? = lastModifiedSupplier()
  override fun loadContents() = loadObjectStream()
  override fun writeContents(content: InputStream) = putObjectStream(content)
}
