package vdi.core.s3.files.data

import vdi.core.s3.files.FileName

interface InstallReadyFile: DataFile {
  override val baseName: String
    get() = FileName.InstallReadyFile
}
