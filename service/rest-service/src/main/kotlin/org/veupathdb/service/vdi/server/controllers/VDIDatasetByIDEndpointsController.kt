package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsVdId
import org.veupathdb.service.vdi.service.datasets.*
import org.veupathdb.vdi.lib.common.field.toUserID

class VDIDatasetByIDEndpointsController(@Context request: ContainerRequest) : VdiDatasetsVdId, ControllerBase(request) {
  @Authenticated(allowGuests = true, adminOverride = ALLOW_ALWAYS)
  override fun getVdiDatasetsByVdId(vdID: String): VdiDatasetsVdId.GetVdiDatasetsByVdIdResponse {
    // We're trusting that we couldn't have gotten here with a null user unless
    // the admin token validation succeeded.
    val user = maybeUser
      ?: return VdiDatasetsVdId.GetVdiDatasetsByVdIdResponse
        .respond200WithApplicationJson(generalGetDatasetByID(vdID.asVDIID()))

    if (user.isGuest) {
      return VdiDatasetsVdId.GetVdiDatasetsByVdIdResponse
        .respond200WithApplicationJson(getCommunityDataset(vdID.asVDIID()))
    }

    return VdiDatasetsVdId.GetVdiDatasetsByVdIdResponse
      .respond200WithApplicationJson(getDatasetByID(userID.toUserID(), vdID.asVDIID()))
  }

  @Authenticated
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
