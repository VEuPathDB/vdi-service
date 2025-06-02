package vdi.core.s3.paths

import vdi.model.data.DatasetID
import vdi.model.data.UserID

sealed interface DatasetPath<T: Enum<T>> {
  val fileName: String
  val pathString: String
  val userID: UserID
  val datasetID: DatasetID
  val bucketName: String
  val type: T
}
