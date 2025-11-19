package vdi.core.s3.files.data

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import java.io.InputStream
import vdi.core.s3.files.LazyDatasetFileImpl

internal class LazyLoadRawUpload(path: String, bucket: ObjectContainer): LazyDatasetFileImpl(path, bucket), RawUploadFile {
  override fun open(): InputStream? =
    bucket.open(path)?.stream
}