package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.CacheDB

internal fun deleteDataset(userID: UserID, datasetID: DatasetID) {
  val ds = CacheDB.selectDataset(datasetID) ?: throw NotFoundException()

  if (ds.ownerID != userID)
    throw ForbiddenException()

  DatasetStore.putDeleteFlag(userID, datasetID)
}