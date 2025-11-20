package vdi.core.s3.files.maps

import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.files.MaterializedObjectRef

internal class MaterializedMapping(s3Object: S3Object): MaterializedObjectRef(s3Object), MappingFile {
  override val contentType: String
    get() = super<MappingFile>.contentType
}