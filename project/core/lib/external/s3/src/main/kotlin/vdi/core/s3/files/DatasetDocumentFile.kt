package vdi.core.s3.files

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.paths.DocumentFileType

sealed interface DatasetDocumentFile: DatasetFile {
  val type: DocumentFileType

  companion object {
    const val DefaultContentType = "application/octet-stream"

    fun create(path: String, bucket: S3Bucket, contentType: String = DefaultContentType): DatasetDocumentFile =
      LazyImpl(path, bucket.objects, contentType)

    fun create(s3Object: S3Object): DatasetDocumentFile = EagerImpl(s3Object)
  }

  private class LazyImpl(
    path: String,
    bucket: ObjectContainer,
    override val contentType: String,
  ): DatasetDocumentFile, LazyDatasetFileImpl(path, bucket) {
    override val type get() = DocumentFileType.Uncategorized
  }

  private class EagerImpl(s3Object: S3Object): DatasetDocumentFile, EagerDatasetFileImpl(s3Object) {
    override val type get() = DocumentFileType.Uncategorized
    override val contentType get() = DefaultContentType
  }
}
