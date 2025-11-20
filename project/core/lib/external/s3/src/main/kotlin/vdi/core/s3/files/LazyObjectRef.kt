package vdi.core.s3.files

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import java.io.InputStream

/**
 * Reference to an expected object in the object store whose existence and
 * metadata are not cached locally, and are instead requested from the object
 * store on access to this class's properties and methods.
 *
 * Properties are looked up on request to avoid situations where an object is
 * removed or modified by another process while an instance of this class is in
 * active use.
 */
internal abstract class LazyObjectRef(
  override val path: String,
  protected val bucket: ObjectContainer,
): DatasetFile {
  override fun lastModified() =
    // This looks weird, but we use list instead of stat since stat only
    // returns seconds resolution, not milliseconds.
    bucket.list(path).firstNotNullOfOrNull { it.lastModified }

  override val contentType: String?
    get() = bucket.stat(path)?.contentType

  override fun exists() =
    path in bucket

  override fun open() =
    bucket.open(path)?.stream

  override fun writeContents(content: InputStream) {
    bucket.put(path) {
      contentType = this@LazyObjectRef.contentType
      stream = content
    }
  }

  override fun put(provider: () -> InputStream) {
    provider().use {
      bucket.put(path) {
        contentType = this@LazyObjectRef.contentType
        stream = it
      }
    }
  }


  override fun delete() = bucket.delete(path)
}