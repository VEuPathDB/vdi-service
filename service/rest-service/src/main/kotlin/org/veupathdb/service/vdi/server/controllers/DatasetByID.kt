package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.errors.UnprocessableEntityException
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequestBody
import org.veupathdb.service.vdi.generated.model.DatasetPutRequestBody
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId.*
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsVdiId
import org.veupathdb.service.vdi.server.inputs.cleanup
import org.veupathdb.service.vdi.server.inputs.validate
import org.veupathdb.service.vdi.server.outputs.BadRequestError
import org.veupathdb.service.vdi.server.outputs.NotFoundError
import org.veupathdb.service.vdi.server.outputs.UnprocessableEntityError
import org.veupathdb.service.vdi.server.services.dataset.*
import org.veupathdb.service.vdi.server.services.dataset.getLatestRevision
import org.veupathdb.service.vdi.server.services.dataset.putDataset
import org.veupathdb.service.vdi.server.services.dataset.updateDatasetMeta
import org.veupathdb.service.vdi.service.dataset.*
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated
class DatasetByID(@Context request: ContainerRequest)
  : DatasetsVdiId
  , VdiDatasetsVdiId // DEPRECATED API
  , ControllerBase(request)
{
  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun getDatasetsByVdiId(rawID: String): GetDatasetsByVdiIdResponse =
    rawID.asVDIID()
      .let { vdiID ->
        when (val userID = maybeUserID) {
          null -> adminGetDatasetByID(vdiID)
          else -> getDatasetByID(userID.toUserID(), vdiID)
        }.let {
          // If the dataset could not be found under the given dataset ID, then
          // check if it has been revised.  If there is a newer revision,
          // redirect to the new dataset ID endpoint.
          it.takeIf { it.status == 404 }
            ?.let { getLatestRevision(vdiID, ::redirectURL) }
            ?: it
        }
      }

  override fun patchDatasetsByVdiId(vdiID: String, entity: DatasetPatchRequestBody?): PatchDatasetsByVdiIdResponse =
    entity?.let { body ->


      updateDatasetMeta(userID.toUserID(), vdiID.asVDIID(), body)
    }
      ?: PatchDatasetsByVdiIdResponse.respond400WithApplicationJson(BadRequestError("request body cannot be empty"))

  override fun putDatasetsByVdiId(vdiId: String, entity: DatasetPutRequestBody?): PutDatasetsByVdiIdResponse =
    entity?.let { body ->
        try {
          putDataset(userID.toUserID(), vdiId.asVDIID(), body)
        } catch (e: BadRequestException) {
          PutDatasetsByVdiIdResponse.respond400WithApplicationJson(BadRequestError(e.message ?: "bad request"))
        } catch (e: NotFoundException) {
          PutDatasetsByVdiIdResponse.respond404WithApplicationJson(NotFoundError())
        }
    } ?: PutDatasetsByVdiIdResponse.respond400WithApplicationJson(BadRequestError("empty request body"))

  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun deleteDatasetsByVdiId(vdiID: String): DeleteDatasetsByVdiIdResponse {
    if (maybeUserID == null)
      adminDeleteDataset(vdiID.asVDIID())
    else
      userDeleteDataset(maybeUserID!!.toUserID(), vdiID.asVDIID())

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
