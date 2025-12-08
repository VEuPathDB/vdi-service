@file:JvmName("DatasetDeleteService")
package vdi.service.rest.server.services.dataset

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.withTransaction
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.service.rest.generated.resources.DatasetsVdiId
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.outputs.ForbiddenError
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.wrap

fun adminDeleteDataset(datasetID: DatasetID): DatasetsVdiId.DeleteDatasetsByVdiIdResponse {
  val ds = CacheDB().selectDataset(datasetID) ?: return Static404.wrap()
  deleteUserDataset(ds.ownerID, datasetID)
  return DatasetsVdiId.DeleteDatasetsByVdiIdResponse.respond204()
}

/**
 * Marks the target dataset as deleted in S3 if it exists and is owned by the
 * requesting user.
 *
 * @param datasetID ID of the dataset to delete.
 *
 * @throws NotFoundException If the target dataset does not exist.
 *
 * @throws ForbiddenException If the target dataset is not owned by the
 * requesting user.
 */
fun <T: ControllerBase> T.userDeleteDataset(datasetID: DatasetID): DatasetsVdiId.DeleteDatasetsByVdiIdResponse {
  // Verify that the target dataset exists.
  val ds = CacheDB().selectDataset(datasetID)
    ?: return Static404.wrap()

  // Verify the dataset is owned by the requesting user.
  if (ds.ownerID != userID)
    return ForbiddenError("cannot delete unowned datasets").wrap()

  return deleteUserDataset(userID, datasetID)
}

private fun deleteUserDataset(userID: UserID, datasetID: DatasetID): DatasetsVdiId.DeleteDatasetsByVdiIdResponse {
  // If the dataset has been revised, don't create a delete flag.
  if (DatasetStore.hasRevisedFlag(userID, datasetID))
    return ForbiddenError("cannot delete old revisions of a live dataset").wrap()

  CacheDB().withTransaction {
    // Put a delete flag in S3 for the target dataset.
    DatasetStore.putDeleteFlag(userID, datasetID)

    // Update the deleted flag in the local pg.
    it.updateDatasetDeleted(datasetID, true)
  }

  return DatasetsVdiId.DeleteDatasetsByVdiIdResponse.respond204()
}
