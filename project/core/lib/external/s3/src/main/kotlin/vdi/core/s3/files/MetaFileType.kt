package vdi.core.s3.files

import vdi.model.data.DatasetManifest
import vdi.model.data.DatasetMetadata

enum class MetaFileType(val fileName: String) {
  Metadata(FileName.MetadataFile),
  Manifest(FileName.ManifestFile);

  val contentType = "application/json"

  val typeDefinition get() = when (this) {
    Metadata -> DatasetMetadata::class
    Manifest -> DatasetManifest::class
  }
}
