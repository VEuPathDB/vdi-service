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
    rawID.asVDIID().let { vdiID ->
      try {
        when (val userID = maybeUserID) {
          null -> GetDatasetsByVdiIdResponse
            .respond200WithApplicationJson(adminGetDatasetByID(vdiID))

          else -> GetDatasetsByVdiIdResponse
            .respond200WithApplicationJson(getDatasetByID(userID.toUserID(), vdiID))
        }
      } catch (e: NotFoundException) {
        getLatestRevision(vdiID)?.let {
          GetDatasetsByVdiIdResponse.respond301(
            GetDatasetsByVdiIdResponse.headersFor301()
              .withLocation(redirectURL(it))
          )
        } ?: GetDatasetsByVdiIdResponse.respond404WithApplicationJson(NotFoundError())
      }
    }

  override fun patchDatasetsByVdiId(vdiID: String, entity: DatasetPatchRequestBody?): PatchDatasetsByVdiIdResponse =
    entity?.let { updateDatasetMeta(userID.toUserID(), vdiID.asVDIID(), it) }
      ?: PatchDatasetsByVdiIdResponse.respond400WithApplicationJson(BadRequestError("request body cannot be empty"))

  override fun putDatasetsByVdiId(vdiId: String, entity: DatasetPutRequestBody?): PutDatasetsByVdiIdResponse {
    entity ?: return PutDatasetsByVdiIdResponse.respond400WithApplicationJson(BadRequestError("empty request body"))
    return try {
      entity.cleanup()
      entity.validate()

      putDataset(userID.toUserID(), vdiId.asVDIID(), )
        .let {
          PutDatasetsByVdiIdResponse.respond201WithApplicationJson(
            it,
            PutDatasetsByVdiIdResponse.headersFor201().withLocation(redirectURL(it.datasetId))
          )
        }
    } catch (e: BadRequestException) {
      PutDatasetsByVdiIdResponse.respond400WithApplicationJson(BadRequestError(e.message ?: "bad request"))
    } catch (e: NotFoundException) {
      PutDatasetsByVdiIdResponse.respond404WithApplicationJson(NotFoundError())
    } catch (e: UnprocessableEntityException) {
      PutDatasetsByVdiIdResponse.respond422WithApplicationJson(UnprocessableEntityError(e))
    }
  }

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
