package org.veupathdb.service.vdi.service.datasets

import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

fun updateDatasetMeta(userID: UserID, datasetID: DatasetID, patch: DatasetPatchRequest) {
  TODO("write new meta to S3")
}