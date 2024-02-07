package org.veupathdb.vdi.lib.s3.datasets.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

sealed interface VDPath {
  val bucketName: String
  val userID: UserID
  val datasetID: DatasetID
}
