package vdi.core.s3.files.data

import vdi.core.s3.files.FileName

interface ImportReadyFile: DataFile {
  override val baseName: String
    get() = FileName.ImportReadyFile
}
