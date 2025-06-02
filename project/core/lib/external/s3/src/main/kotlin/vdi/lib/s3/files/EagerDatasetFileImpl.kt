package vdi.lib.s3.files

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object
import java.io.InputStream
import java.time.OffsetDateTime

internal sealed class EagerDatasetFileImpl(
  override val path: String,
  private val lastModified: OffsetDateTime,
  protected val bucket: ObjectContainer,
) : DatasetFile {
  constructor(s3Object: S3Object) : this(
    path         = s3Object.path,
    lastModified = s3Object.lastModified!!,
    bucket       = s3Object.bucket.objects
  )

  override fun exists() = true
  override fun loadContents() = bucket.open(path)?.stream
  override fun lastModified() = lastModified
  override fun writeContents(content: InputStream) = throw UnsupportedOperationException()
  override fun delete() = throw UnsupportedOperationException()
}
