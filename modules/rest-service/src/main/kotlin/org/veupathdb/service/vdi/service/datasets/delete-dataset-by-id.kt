package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.CacheDB

internal fun adminDeleteDataset(datasetID: DatasetID) {
  val ds = CacheDB.selectDataset(datasetID) ?: throw NotFoundException()
  deleteUserDataset(ds.ownerID, datasetID)
}

/**
 * Marks the target dataset as deleted in S3 if it exists and is owned by the
 * requesting user.
 *
 * @param userID ID of the requesting user.
 *
 * @param datasetID ID of the dataset to delete.
 *
 * @throws NotFoundException If the target dataset does not exist.
 *
 * @throws ForbiddenException If the target dataset is not owned by the
 * requesting user.
 */
internal fun userDeleteDataset(userID: UserID, datasetID: DatasetID) {
  // Verify that the target dataset exists.
  val ds = CacheDB.selectDataset(datasetID) ?: throw NotFoundException()

  // Verify the dataset is owned by the requesting user.
  if (ds.ownerID != userID)
    throw ForbiddenException()

  deleteUserDataset(userID, datasetID)
}

private fun deleteUserDataset(userID: UserID, datasetID: DatasetID) {
  CacheDB.withTransaction {
    // Update the deleted flag in the local pg.
    it.updateDatasetDeleted(datasetID, true)

    // Put a delete flag in S3 for the target dataset.
    DatasetStore.putDeleteFlag(userID, datasetID)
  }
}