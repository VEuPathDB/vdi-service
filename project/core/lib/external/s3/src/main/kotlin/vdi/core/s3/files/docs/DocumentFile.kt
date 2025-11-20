package vdi.core.s3.files.docs

import vdi.core.s3.files.DatasetFile

interface DocumentFile: DatasetFile {
  companion object {
    internal const val ContentType = "application/octet-stream"
  }
}

