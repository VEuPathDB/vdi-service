package vdi.core.s3.files.data

import vdi.core.s3.files.DatasetFile

sealed interface DataFile: DatasetFile {
  override val contentType: String
    get() = "application/zip"
}
