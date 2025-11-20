package vdi.core.s3.files.flags

import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.files.MaterializedObjectRef

internal class MaterializedDeleteFlag(s3Object: S3Object): MaterializedObjectRef(s3Object), DeleteFlagFile {
  override val contentType: String
    get() = super<DeleteFlagFile>.contentType
}
