package vdi.core.s3.files.meta

import vdi.core.s3.files.DatasetFile

sealed interface MetaFile<T: Any>: DatasetFile {
  fun load(): T?

  fun put(value: T)
}

