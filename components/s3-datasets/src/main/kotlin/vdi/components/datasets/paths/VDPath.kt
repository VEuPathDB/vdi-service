package vdi.components.datasets.paths

import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

sealed interface VDPath {
  val bucketName: String
  val rootSegment: String
  val userID: UserID
  val datasetID: DatasetID
}

