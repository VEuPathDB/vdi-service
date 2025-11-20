package vdi.core.s3.files.data

import org.veupathdb.lib.s3.s34k.objects.S3Object
import java.io.InputStream
import vdi.core.s3.files.MaterializedObjectRef

internal class MaterializedImportZip(s3Object: S3Object): MaterializedObjectRef(s3Object), ImportReadyFile {
  override val contentType: String
    get() = super<ImportReadyFile>.contentType

  override fun open(): InputStream? =
    bucket.open(path)?.stream
}