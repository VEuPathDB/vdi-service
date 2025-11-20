package vdi.core.s3.files.maps

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import vdi.core.s3.files.LazyObjectRef

internal class LazyLoadMapping(path: String, bucket: ObjectContainer): LazyObjectRef(path, bucket), MappingFile {
  override val contentType: String
    get() = super<MappingFile>.contentType
}
