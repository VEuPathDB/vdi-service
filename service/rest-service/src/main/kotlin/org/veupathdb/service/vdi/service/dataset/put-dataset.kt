package org.veupathdb.service.vdi.service.dataset

import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.generated.model.DatasetPutRequestBody
import org.veupathdb.service.vdi.generated.model.DatasetPutResponseBody
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.CacheDB

internal fun putDataset(
  userID: UserID,
  datasetID: DatasetID,
  request: DatasetPutRequestBody,
): DatasetPutResponseBody {
  val originalDataset = CacheDB().selectDatasetForUser(userID, datasetID)
    ?: throw NotFoundException()


}
