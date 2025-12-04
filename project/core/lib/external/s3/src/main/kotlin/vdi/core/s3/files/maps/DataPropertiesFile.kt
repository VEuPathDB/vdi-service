package vdi.core.s3.files.maps

import vdi.core.s3.files.DatasetFile

interface DataPropertiesFile: DatasetFile {
  override val contentType: String
    get() = ContentType

  companion object {
    internal const val ContentType = "text/tab-separated-values"
  }
}

