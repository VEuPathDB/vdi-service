package vdi.core.s3.files.meta

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.files.EagerDatasetFileImpl
import vdi.json.JSON
import vdi.model.meta.DatasetManifest

internal class MaterializedManifest(s3Object: S3Object): EagerDatasetFileImpl(s3Object), ManifestFile {
  override fun load(): DatasetManifest? =
    bucket.open(path)
      ?.stream
      ?.use { JSON.readValue(it) }
}