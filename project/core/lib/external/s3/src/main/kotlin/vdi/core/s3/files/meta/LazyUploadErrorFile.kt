package vdi.core.s3.files.meta

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import vdi.core.s3.files.LazyObjectRef
import vdi.json.JSON
import vdi.model.misc.UploadErrorReport

internal class LazyUploadErrorFile(path: String, bucket: ObjectContainer): LazyObjectRef(path, bucket), UploadErrorFile {
  override val contentType: String
    get() = super<UploadErrorFile>.contentType

  override fun load(): UploadErrorReport? =
    bucket.open(path)
      ?.stream
      ?.use { JSON.readValue(it) }
}