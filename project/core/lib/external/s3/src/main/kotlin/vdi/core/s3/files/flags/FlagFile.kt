package vdi.core.s3.files.flags

import vdi.core.s3.files.DatasetFile

sealed interface FlagFile: DatasetFile {
  override val contentType: String
    get() = "text/plain"

  fun create()
}

