package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequestBody
import org.veupathdb.service.vdi.generated.model.DatasetPutRequestBody
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId.*
import org.veupathdb.service.vdi.service.dataset.*
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated
class DatasetByID(@Context request: ContainerRequest) : DatasetsVdiId, ControllerBase(request) {

  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun getDatasetsByVdiId(vdiID: String): GetDatasetsByVdiIdResponse {
    val userID = maybeUserID
      ?: return GetDatasetsByVdiIdResponse
        .respond200WithApplicationJson(adminGetDatasetByID(vdiID.asVDIID()))

    return GetDatasetsByVdiIdResponse
      .respond200WithApplicationJson(getDatasetByID(userID.toUserID(), vdiID.asVDIID()))
  }

  override fun patchDatasetsByVdiId(vdiID: String, entity: DatasetPatchRequestBody): PatchDatasetsByVdiIdResponse {
    updateDatasetMeta(userID.toUserID(), vdiID.asVDIID(), entity)
    return PatchDatasetsByVdiIdResponse.respond204()
  }

  override fun putDatasetsByVdiId(vdiId: String, entity: DatasetPutRequestBody?): PutDatasetsByVdiIdResponse {
    TODO("Not yet implemented")
  }

  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun deleteDatasetsByVdiId(vdiID: String): DeleteDatasetsByVdiIdResponse {
    if (maybeUserID == null)
      adminDeleteDataset(vdiID.asVDIID())
    else
      userDeleteDataset(maybeUserID!!.toUserID(), vdiID.asVDIID())

    return DeleteDatasetsByVdiIdResponse.respond204()
  }
}
