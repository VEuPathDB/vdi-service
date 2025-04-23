package vdi.service.rest.server.services.admin

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.service.rest.generated.model.DatasetObjectPurgeRequestBody
import vdi.service.rest.generated.resources.AdminRpc.PostAdminRpcObjectStorePurgeDatasetResponse
import vdi.service.s3.DatasetStore
import vdi.service.server.outputs.BadRequestError
import vdi.service.server.outputs.wrap

internal fun purgeDataset(request: vdi.service.rest.generated.model.DatasetObjectPurgeRequestBody): PostAdminRpcObjectStorePurgeDatasetResponse {
  if (request.userId == null || request.datasetId == null)
    return BadRequestError("request body missing one or more fields").wrap()

  val datasetID = DatasetID(request.datasetId)

  for (obj in DatasetStore.listObjectsForDataset(request.userId.toUserID(), datasetID))
    obj.delete()

  return PostAdminRpcObjectStorePurgeDatasetResponse.respond204()
}
