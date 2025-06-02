package vdi.lib.s3.files

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object

sealed interface DatasetDataFile: DatasetFile {
  val type: DataFileType

  override val contentType: String
    get() = "application/zip"

  companion object {
    fun create(path: String, bucket: S3Bucket): DatasetDataFile = LazyDataFile(path, bucket.objects)
    fun create(s3Object: S3Object): DatasetDataFile = EagerDataFile(s3Object)
  }

  private class LazyDataFile(path: String, bucket: ObjectContainer): LazyDatasetFileImpl(path, bucket), DatasetDataFile {
    override val type = baseName.let { name -> DataFileType.entries.firstOrNull { it.fileName == name } }
      ?: throw IllegalArgumentException("unrecognized dataset object path $path")
  }

  private class EagerDataFile(s3Object: S3Object): EagerDatasetFileImpl(s3Object), DatasetDataFile {
    override val type = baseName.let { name -> DataFileType.entries.firstOrNull { it.fileName == name } }
      ?: throw IllegalArgumentException("unrecognized dataset object path $path")
  }
}
