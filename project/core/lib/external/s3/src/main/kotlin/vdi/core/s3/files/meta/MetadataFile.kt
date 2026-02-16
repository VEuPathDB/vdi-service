package vdi.core.s3.files.meta

import vdi.core.s3.files.FileName
import vdi.model.meta.DatasetMetadata

interface MetadataFile: VDIMetaFile<DatasetMetadata> {
  override val baseName: String
    get() = FileName.MetadataFile
}
