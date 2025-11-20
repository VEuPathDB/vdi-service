package vdi.core.s3.files.meta

import vdi.core.s3.files.FileName
import vdi.model.meta.DatasetManifest

interface ManifestFile: MetaFile<DatasetManifest> {
  override val baseName: String
    get() = FileName.ManifestFile
}
