package vdi.core.s3.files.data

import java.io.InputStream
import vdi.core.s3.files.DatasetFile

sealed interface DataFile: DatasetFile {
  override val contentType: String
    get() = "application/zip"

  fun open(): InputStream?

  fun put(content: () -> InputStream)
}
