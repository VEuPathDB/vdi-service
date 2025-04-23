package vdi.service.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import vdi.service.generated.model.DatasetPatchRequestBody
import vdi.service.generated.model.DatasetPutRequestBody
import vdi.service.generated.resources.DatasetsVdiId
import vdi.service.generated.resources.DatasetsVdiId.*
import vdi.service.generated.resources.DatasetsVdiIdFiles
import vdi.service.generated.resources.VdiDatasetsVdiId
import vdi.service.server.outputs.BadRequestError
import vdi.service.server.outputs.wrap
import vdi.service.server.services.dataset.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated
class DatasetByID(@Context request: ContainerRequest)
  : DatasetsVdiId
  , VdiDatasetsVdiId // DEPRECATED API
  , ControllerBase(request)
{
  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun getDatasetsByVdiId(rawID: String): GetDatasetsByVdiIdResponse =
    DatasetID(rawID).let { vdiID ->
      when (val userID = maybeUserID) {
        null -> adminGetDatasetByID(vdiID)
        else -> getDatasetByID(userID.toUserID(), vdiID)
      }.let {
        if (it.isLeft) {
          it.unwrapLeft()
            .apply { revisionHistory?.forEach { e -> e.fileListUrl = createURL(DatasetsVdiIdFiles.ROOT_PATH.replace(VDI_ID_VAR, e.revisionId)) } }
            .let(GetDatasetsByVdiIdResponse::respond200WithApplicationJson)
        } else {
          // If the dataset could not be found under the given dataset ID, then
          // check if it has been revised.  If there is a newer revision,
          // redirect to the new dataset ID endpoint.
          it.unwrapRight()
            .takeIf { it.status == 404 }
            ?.let { getLatestRevision(vdiID, ::redirectURL) }
            ?: it.unwrapRight()
        }
      }
    }

  override fun patchDatasetsByVdiId(vdiID: String, entity: DatasetPatchRequestBody?) =
    entity?.let { body -> updateDatasetMeta(userID.toUserID(), DatasetID(vdiID), body) }
      ?: PatchDatasetsByVdiIdResponse.respond400WithApplicationJson(BadRequestError("request body cannot be empty"))!!

  override fun putDatasetsByVdiId(vdiId: String, entity: DatasetPutRequestBody?) =
    entity
      ?.let { body -> putDataset(userID.toUserID(), DatasetID(vdiId), body) }
      ?.let {
        if (it.isLeft)
          it.unwrapLeft()
            .let { body -> PutDatasetsByVdiIdResponse.respond201WithApplicationJson(
              body,
              PutDatasetsByVdiIdResponse.headersFor201()
                .withLocation(ROOT_PATH.replace(VDI_ID_VAR, body.datasetId))
            ) }
        else
          it.unwrapRight()
      }
      ?: BadRequestError("empty request body").wrap()

  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun deleteDatasetsByVdiId(vdiID: String): DeleteDatasetsByVdiIdResponse {
    if (maybeUserID == null)
      adminDeleteDataset(DatasetID(vdiID))
    else
      userDeleteDataset(maybeUserID!!.toUserID(), DatasetID(vdiID))

    return DeleteDatasetsByVdiIdResponse.respond204()
  }

  // DEPRECATED API
  // DEPRECATED API
  // DEPRECATED API
  // DEPRECATED API

  override fun getVdiDatasetsByVdiId(vdiId: String) =
    VdiDatasetsVdiId.GetVdiDatasetsByVdiIdResponse(getDatasetsByVdiId(vdiId))

  override fun patchVdiDatasetsByVdiId(vdiId: String, entity: DatasetPatchRequestBody?) =
    VdiDatasetsVdiId.PatchVdiDatasetsByVdiIdResponse(patchDatasetsByVdiId(vdiId, entity))

  override fun putVdiDatasetsByVdiId(vdiId: String, entity: DatasetPutRequestBody?) =
    VdiDatasetsVdiId.PutVdiDatasetsByVdiIdResponse(putDatasetsByVdiId(vdiId, entity))

  override fun deleteVdiDatasetsByVdiId(vdiId: String) =
    VdiDatasetsVdiId.DeleteVdiDatasetsByVdiIdResponse(deleteDatasetsByVdiId(vdiId))
}
