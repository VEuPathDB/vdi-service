package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsVdId
import org.veupathdb.service.vdi.service.dataset.*
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated
class DatasetByID(@Context request: ContainerRequest) : VdiDatasetsVdId, ControllerBase(request) {

  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun getVdiDatasetsByVdId(vdID: String): VdiDatasetsVdId.GetVdiDatasetsByVdIdResponse {
    val userID = maybeUserID
      ?: return VdiDatasetsVdId.GetVdiDatasetsByVdIdResponse
        .respond200WithApplicationJson(adminGetDatasetByID(vdID.asVDIID()))

    return VdiDatasetsVdId.GetVdiDatasetsByVdIdResponse
      .respond200WithApplicationJson(getDatasetByID(userID.toUserID(), vdID.asVDIID()))
  }

  override fun patchVdiDatasetsByVdId(vdID: String, entity: DatasetPatchRequest): VdiDatasetsVdId.PatchVdiDatasetsByVdIdResponse {
    updateDatasetMeta(userID.toUserID(), vdID.asVDIID(), entity)
    return VdiDatasetsVdId.PatchVdiDatasetsByVdIdResponse.respond204()
  }

  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun deleteVdiDatasetsByVdId(vdID: String): VdiDatasetsVdId.DeleteVdiDatasetsByVdIdResponse {
    if (maybeUserID == null)
      adminDeleteDataset(vdID.asVDIID())
    else
      userDeleteDataset(maybeUserID!!.toUserID(), vdID.asVDIID())

    return VdiDatasetsVdId.DeleteVdiDatasetsByVdIdResponse.respond204()
  }
}
