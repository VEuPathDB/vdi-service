package vdi.core.s3.files.meta

import java.io.ByteArrayInputStream
import vdi.core.s3.files.DatasetFile
import vdi.json.JSON

sealed interface VDIMetaFile<T: Any>: DatasetFile {
  override val contentType: String
    get() = "application/json"

  fun load(): T?

  fun put(value: T) =
    put { ByteArrayInputStream(JSON.writeValueAsBytes(value)) }
}

