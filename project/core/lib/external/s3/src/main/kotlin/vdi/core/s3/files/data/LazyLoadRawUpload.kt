package vdi.core.s3.files.data

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import java.io.InputStream
import vdi.core.s3.files.LazyObjectRef

internal class LazyLoadRawUpload(path: String, bucket: ObjectContainer): LazyObjectRef(path, bucket), RawUploadFile {
  override val contentType: String
    get() = super<RawUploadFile>.contentType

  override fun open(): InputStream? =
    bucket.open(path)?.stream
}