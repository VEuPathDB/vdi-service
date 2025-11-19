package vdi.core.s3.files.maps

import java.io.InputStream
import vdi.core.s3.files.DatasetFile

interface MappingFile: DatasetFile {
  override val contentType: String
    get() = "text/tab-separated-values"

  fun open(): InputStream?

  fun put(content: () -> InputStream)
}

