package vdi.core.s3.files.meta

import vdi.core.s3.files.FileName
import vdi.model.misc.UploadErrorReport

interface UploadErrorFile: VDIMetaFile<UploadErrorReport> {
  override val baseName: String
    get() = FileName.UploadErrorFile
}