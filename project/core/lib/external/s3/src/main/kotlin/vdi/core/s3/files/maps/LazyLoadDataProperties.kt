package vdi.core.s3.files.maps

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import vdi.core.s3.files.LazyObjectRef

internal class LazyLoadDataProperties(path: String, bucket: ObjectContainer): LazyObjectRef(path, bucket), DataPropertiesFile {
  override val contentType: String
    get() = super<DataPropertiesFile>.contentType
}
