package vdi.core.s3.files.data

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import java.io.InputStream
import vdi.core.s3.files.LazyObjectRef

internal class LazyLoadInstallZip(path: String, bucket: ObjectContainer): LazyObjectRef(path, bucket), InstallReadyFile {
  override val contentType: String
    get() = super<InstallReadyFile>.contentType

  override fun open(): InputStream? =
    bucket.open(path)?.stream
}