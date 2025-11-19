package vdi.core.s3.files.data

import vdi.core.s3.files.FileName

interface RawUploadFile: DataFile {
  override val baseName: String
    get() = FileName.RawUploadFile
}
