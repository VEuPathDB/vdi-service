package vdi.core.s3.files.meta

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.files.EagerDatasetFileImpl
import vdi.json.JSON
import vdi.model.meta.DatasetMetadata

internal class MaterializedMetaFile(s3Object: S3Object): EagerDatasetFileImpl(s3Object), MetadataFile {
  override fun load(): DatasetMetadata? =
    bucket.open(path)
      ?.stream
      ?.use { JSON.readValue(it) }
}