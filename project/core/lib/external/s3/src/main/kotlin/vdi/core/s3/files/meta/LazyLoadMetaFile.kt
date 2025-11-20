package vdi.core.s3.files.meta

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import vdi.core.s3.files.LazyObjectRef
import vdi.json.JSON
import vdi.model.meta.DatasetMetadata

internal class LazyLoadMetaFile(path: String, bucket: ObjectContainer): LazyObjectRef(path, bucket), MetadataFile {
  override val contentType: String
    get() = super<MetadataFile>.contentType

  override fun load(): DatasetMetadata? =
    bucket.open(path)
      ?.stream
      ?.use { JSON.readValue(it) }
}