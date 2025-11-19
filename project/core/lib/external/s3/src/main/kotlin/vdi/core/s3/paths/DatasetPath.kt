package vdi.core.s3.paths

import vdi.model.meta.DatasetID
import vdi.model.meta.UserID

sealed interface DatasetPath {
  val fileName: String
  val pathString: String
  val userID: UserID
  val datasetID: DatasetID
  val bucketName: String
}
