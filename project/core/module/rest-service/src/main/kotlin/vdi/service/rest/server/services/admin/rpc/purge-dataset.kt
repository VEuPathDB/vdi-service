package vdi.service.rest.server.services.admin.rpc

import vdi.model.data.DatasetID
import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.service.rest.generated.model.DatasetObjectPurgeRequestBody
import vdi.service.rest.generated.resources.AdminRpc.PostAdminRpcObjectStorePurgeDatasetResponse
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.wrap

internal fun purgeDataset(request: DatasetObjectPurgeRequestBody): PostAdminRpcObjectStorePurgeDatasetResponse {
  if (request.userId == null || request.datasetId == null)
    return BadRequestError("request body missing one or more fields").wrap()

  val datasetID = DatasetID(request.datasetId)

  for (obj in DatasetStore.listObjectsForDataset(request.userId.toUserID(), datasetID))
    obj.delete()

  return PostAdminRpcObjectStorePurgeDatasetResponse.respond204()
}
