package vdi.core.s3.files.meta

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.files.MaterializedObjectRef
import vdi.json.JSON
import vdi.model.misc.UploadErrorReport

internal class MaterializedUploadErrorFile(s3Object: S3Object): MaterializedObjectRef(s3Object), UploadErrorFile {
  override val contentType: String
    get() = super<UploadErrorFile>.contentType

  override fun load(): UploadErrorReport? =
    bucket.open(path)
      ?.stream
      ?.use { JSON.readValue(it) }
}