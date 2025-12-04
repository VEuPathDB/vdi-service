package vdi.core.s3.files.maps

import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.files.MaterializedObjectRef

internal class MaterializedDataProperties(s3Object: S3Object): MaterializedObjectRef(s3Object), DataPropertiesFile {
  override val contentType: String
    get() = super<DataPropertiesFile>.contentType
}