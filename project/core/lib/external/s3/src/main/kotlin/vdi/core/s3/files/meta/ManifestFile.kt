package vdi.core.s3.files.meta

import vdi.core.s3.files.FileName
import vdi.model.meta.DatasetManifest

interface ManifestFile: VDIMetaFile<DatasetManifest> {
  override val baseName: String
    get() = FileName.ManifestFile
}
