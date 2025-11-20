package vdi.core.s3.files.flags

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import vdi.core.s3.files.LazyObjectRef

internal class LazyLoadDeleteFlag(path: String, bucket: ObjectContainer): LazyObjectRef(path, bucket), DeleteFlagFile {
  override val contentType: String
    get() = super<DeleteFlagFile>.contentType
}
