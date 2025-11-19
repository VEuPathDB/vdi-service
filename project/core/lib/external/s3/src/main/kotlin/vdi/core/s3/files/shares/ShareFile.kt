package vdi.core.s3.files.shares

import vdi.core.s3.files.DatasetFile
import vdi.model.meta.UserID

sealed interface ShareFile<T: Any>: DatasetFile {
  val recipientID: UserID

  fun load(): T?

  override val contentType: String
    get() = "application/json"
}
