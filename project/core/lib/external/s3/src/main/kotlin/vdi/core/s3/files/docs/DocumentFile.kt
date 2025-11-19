package vdi.core.s3.files.docs

import java.io.InputStream
import vdi.core.s3.files.DatasetFile

interface DocumentFile: DatasetFile {
  override val contentType: String
    get() = "application/octet-stream"

  fun open(): InputStream?

  fun put(content: () -> InputStream)
}

