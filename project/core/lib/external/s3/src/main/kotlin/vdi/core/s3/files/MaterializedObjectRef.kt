package vdi.core.s3.files

import org.veupathdb.lib.s3.s34k.objects.S3Object
import java.io.InputStream

/**
 * Reference to an object that was guaranteed to have existed in the object
 * store by way of appearing in a bulk lookup.
 */
internal abstract class MaterializedObjectRef(s3Object: S3Object): DatasetFile {
  private val lastModified = s3Object.lastModified!!

  protected val bucket = s3Object.bucket.objects

  override val path = s3Object.path

  override val contentType: String? = null

  /**
   * Reports `true` regardless of whether the object still exists, as the object
   * _did_ exist when the lookup was performed.
   */
  override fun exists() = true

  /**
   * Attempts to open a stream from the target object's contents.
   */
  override fun open() = bucket.open(path)?.stream

  /**
   * Reports the last modified timestamp of the object at the time of the bulk
   * lookup.
   */
  override fun lastModified() = lastModified

  override fun writeContents(content: InputStream) = throw UnsupportedOperationException()

  /**
   * Write operations are not supported for materialized object references.
   */
  override fun put(provider: () -> InputStream): Nothing =
    throw UnsupportedOperationException()

  /**
   * Write operations are not supported for materialized object references.
   */
  override fun delete(): Nothing =
    throw UnsupportedOperationException()
}
