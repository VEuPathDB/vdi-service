package vdi.core.s3.files.flags

import vdi.core.s3.files.FileName

interface DeleteFlagFile: FlagFile {
  override val baseName: String
    get() = FileName.DeleteFlagFile
}
