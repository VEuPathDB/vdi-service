package org.veupathdb.service.vdi.service.dataset

import org.veupathdb.service.vdi.generated.model.DatasetPutRequestBody
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId.PutDatasetsByVdiIdResponse
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId.PutDatasetsByVdiIdResponse.*
import org.veupathdb.service.vdi.server.outputs.ForbiddenError
import org.veupathdb.service.vdi.server.outputs.NotFoundError
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.CacheDB

internal fun putDataset(
  userID: UserID,
  datasetID: DatasetID,
  request: DatasetPutRequestBody,
): PutDatasetsByVdiIdResponse {
  val originalDataset = CacheDB().selectDatasetForUser(userID, datasetID)
    ?: return respond404WithApplicationJson(NotFoundError())

  if (originalDataset.isDeleted)
    return respond403WithApplicationJson(ForbiddenError("cannot add revisions to a deleted dataset"))

}
