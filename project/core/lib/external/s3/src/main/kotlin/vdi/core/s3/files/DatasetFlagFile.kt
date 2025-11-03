package vdi.core.s3.files

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object
import java.io.InputStream

sealed interface DatasetFlagFile: DatasetFile {
  val type: FlagFileType

  fun touch()

  override val contentType: String
    get() = "text/plain"

  companion object {
    fun create(path: String, bucket: S3Bucket): DatasetFlagFile = LazyImpl(path, bucket.objects)
    fun create(s3Object: S3Object): DatasetFlagFile = EagerImpl(s3Object)
    fun void(path: String): DatasetFlagFile = VoidImpl(path)
  }

  private class LazyImpl(path: String, bucket: ObjectContainer): DatasetFlagFile, LazyDatasetFileImpl(path, bucket) {
    override val type = baseName.let { name -> FlagFileType.entries.firstOrNull { it.fileName == name } }
      ?: throw IllegalArgumentException("unrecognized dataset object path $path")

    override fun touch() { bucket.touch(path) {
      contentType = this@LazyImpl.contentType
    } }
  }

  private class EagerImpl(s3Object: S3Object): DatasetFlagFile, EagerDatasetFileImpl(s3Object) {
    override val type = baseName.let { name -> FlagFileType.entries.firstOrNull { it.fileName == name } }
      ?: throw IllegalArgumentException("unrecognized dataset object path $path")

    override fun touch() = throw UnsupportedOperationException()
  }

  @JvmInline
  private value class VoidImpl(override val path: String): DatasetFlagFile {
    override val type
      get() = baseName.let { name -> FlagFileType.entries.firstOrNull { it.fileName == name } }
        ?: throw IllegalArgumentException("unrecognized dataset object path $path")

    override fun touch() = throw UnsupportedOperationException()
    override fun exists() = false
    override fun lastModified() = null
    override fun loadContents(): InputStream? {
      TODO("Not yet implemented")
    }

    override fun writeContents(content: InputStream) {
      TODO("Not yet implemented")
    }

    override fun delete() {
      TODO("Not yet implemented")
    }

  }
}
