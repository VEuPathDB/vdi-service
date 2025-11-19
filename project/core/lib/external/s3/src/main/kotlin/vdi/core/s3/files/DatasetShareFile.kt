package vdi.core.s3.files

import vdi.model.meta.UserID

sealed interface DatasetShareFile<T: Any>: DatasetFile {
  val recipientID: UserID

  fun load(): T?
}
