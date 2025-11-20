package vdi.core.s3.files.flags

import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.files.MaterializedObjectRef
import vdi.core.s3.files.flags.DeleteFlagFile

internal class MaterializedRevisedFlag(s3Object: S3Object): MaterializedObjectRef(s3Object), RevisedFlagFile {
  override val contentType: String
    get() = super<RevisedFlagFile>.contentType
}