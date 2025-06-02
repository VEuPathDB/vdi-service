package vdi.core.s3.files

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.model.data.UserID
import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt
import vdi.json.JSON

sealed interface DatasetShareFile<T: Any>: DatasetFile {
  val recipientID: UserID

  val type: ShareFileType

  fun load(): T?

  companion object {
    fun create(path: String, recipientID: UserID, bucket: S3Bucket): DatasetShareFile<Any> =
      LazyImpl(path, recipientID, bucket.objects)
    fun create(s3Object: S3Object, recipientID: UserID): DatasetShareFile<Any> =
      EagerImpl(s3Object, recipientID)

    fun createOffer(path: String, recipientID: UserID, bucket: S3Bucket): DatasetShareFile<DatasetShareOffer> =
      LazyImpl(path, recipientID, bucket.objects)
    fun createOffer(s3Object: S3Object, recipientID: UserID): DatasetShareFile<DatasetShareOffer> =
      EagerImpl(s3Object, recipientID)

    fun createReceipt(path: String, recipientID: UserID, bucket: S3Bucket): DatasetShareFile<DatasetShareReceipt> =
      LazyImpl(path, recipientID, bucket.objects)
    fun createReceipt(s3Object: S3Object, recipientID: UserID): DatasetShareFile<DatasetShareReceipt> =
      EagerImpl(s3Object, recipientID)
  }

  private class LazyImpl<T: Any>(
    path: String,
    override val recipientID: UserID,
    bucket: ObjectContainer,
  ): DatasetShareFile<T>, LazyDatasetFileImpl(path, bucket) {
    override val type = baseName.let { name -> ShareFileType.entries.firstOrNull { it.fileName == name } }
      ?: throw IllegalArgumentException("unrecognized dataset object path $path")
    override val contentType get() = type.contentType
    @Suppress("UNCHECKED_CAST")
    override fun load() = bucket.open(path)?.stream?.use { JSON.readValue(it, type.typeDefinition.java) as T }
  }

  private class EagerImpl<T: Any>(
    s3Object: S3Object,
    override val recipientID: UserID
  ): DatasetShareFile<T>, EagerDatasetFileImpl(s3Object) {
    override val type = baseName.let { name -> ShareFileType.entries.firstOrNull { it.fileName == name } }
      ?: throw IllegalArgumentException("unrecognized dataset object path $path")
    override val contentType get() = type.contentType
    @Suppress("UNCHECKED_CAST")
    override fun load() = bucket.open(path)?.stream?.use { JSON.readValue(it, type.typeDefinition.java) as T }
  }
}
