package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

sealed interface DatasetPath {
  val bucketName: String
  val userID: UserID
  val datasetID: DatasetID
  val file: S3File
}
