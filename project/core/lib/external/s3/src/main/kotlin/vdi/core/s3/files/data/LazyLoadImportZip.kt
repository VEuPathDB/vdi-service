package vdi.core.s3.files.data

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import java.io.InputStream
import vdi.core.s3.files.LazyObjectRef

internal class LazyLoadImportZip(path: String, bucket: ObjectContainer): LazyObjectRef(path, bucket), ImportReadyFile {
  override val contentType: String
    get() = super<ImportReadyFile>.contentType

  override fun open(): InputStream? =
    bucket.open(path)?.stream
}