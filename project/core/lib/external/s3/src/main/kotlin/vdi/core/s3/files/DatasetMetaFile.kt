package vdi.core.s3.files

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.model.data.DatasetManifest
import vdi.model.data.DatasetMetadata
import vdi.json.JSON
import vdi.core.s3.files.MetaFileType

sealed interface DatasetMetaFile<T: Any>: DatasetFile {
  val type: MetaFileType

  fun load(): T?

  fun put(value: T)

  companion object {
    fun create(path: String, bucket: S3Bucket): DatasetMetaFile<Any> =
      LazyImpl(path, bucket.objects)
    fun create(s3Object: S3Object): DatasetMetaFile<Any> =
      EagerImpl(s3Object)

    fun createMetadata(path: String, bucket: S3Bucket): DatasetMetaFile<DatasetMetadata> =
      LazyImpl(path, bucket.objects)
    fun createMetadata(s3Object: S3Object): DatasetMetaFile<DatasetMetadata> =
      EagerImpl(s3Object)

    fun createManifest(path: String, bucket: S3Bucket): DatasetMetaFile<DatasetManifest> =
      LazyImpl(path, bucket.objects)
    fun createManifest(s3Object: S3Object): DatasetMetaFile<DatasetManifest> =
      EagerImpl(s3Object)
  }

  private class LazyImpl<T: Any>(
    path: String,
    bucket: ObjectContainer,
  ): DatasetMetaFile<T>, LazyDatasetFileImpl(path, bucket) {
    override val type = baseName.let { name -> MetaFileType.entries.firstOrNull { it.fileName == name } }
      ?: throw IllegalArgumentException("unrecognized dataset object path $path")

    override val contentType
      get() = type.contentType

    @Suppress("UNCHECKED_CAST")
    override fun load() =
      bucket.open(path)?.stream?.use { JSON.readValue(it, type.typeDefinition.java) as T }

    override fun put(value: T) = writeContents(JSON.writeValueAsBytes(value).inputStream())
  }

  private class EagerImpl<T: Any>(s3Object: S3Object): DatasetMetaFile<T>, EagerDatasetFileImpl(s3Object) {
    override val type = baseName.let { name -> MetaFileType.entries.firstOrNull { it.fileName == name } }
      ?: throw IllegalArgumentException("unrecognized dataset object path $path")
    override val contentType
      get() = type.contentType
    @Suppress("UNCHECKED_CAST")
    override fun load() =
      bucket.open(path)?.stream?.use { JSON.readValue(it, type.typeDefinition.java) as T }
    override fun put(value: T) = throw UnsupportedOperationException()
  }
}
