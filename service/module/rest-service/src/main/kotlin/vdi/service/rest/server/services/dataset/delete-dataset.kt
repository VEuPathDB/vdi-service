package vdi.service.rest.server.services.dataset

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.withTransaction
import vdi.service.rest.generated.resources.DatasetsVdiId
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
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
  val ds = CacheDB().selectDataset(datasetID) ?: return Static404.wrap()

  // Verify the dataset is owned by the requesting user.
  if (ds.ownerID != userID)
    throw ForbiddenException()

  deleteUserDataset(userID, datasetID)
  return DatasetsVdiId.DeleteDatasetsByVdiIdResponse.respond204()
}

private fun deleteUserDataset(userID: UserID, datasetID: DatasetID) {
  CacheDB().withTransaction {
    // Put a delete flag in S3 for the target dataset.
    DatasetStore.putDeleteFlag(userID, datasetID)

    // Update the deleted flag in the local pg.
    it.updateDatasetDeleted(datasetID, true)
  }
}
