package vdi.lib.s3.files

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import java.io.InputStream

internal sealed class LazyDatasetFileImpl(
  override val path: String,
  protected val bucket: ObjectContainer,
): DatasetFile {
  override fun lastModified() =
    // This looks weird, but we use list instead of stat since stat only returns seconds resolution, not milliseconds.
    bucket.list(path).firstNotNullOfOrNull { it.lastModified }

  override fun exists() =
    path in bucket

  override fun loadContents() =
    bucket.open(path)?.stream

  override fun writeContents(content: InputStream) {
    bucket.put(path) {
      contentType = this@LazyDatasetFileImpl.contentType
      stream = content
    }
  }

  override fun delete() = bucket.delete(path)
}