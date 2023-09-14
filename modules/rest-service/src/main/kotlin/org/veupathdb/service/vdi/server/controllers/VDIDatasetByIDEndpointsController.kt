package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.AllowAdminAuth
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsVdId
import org.veupathdb.service.vdi.service.datasets.deleteDataset
import org.veupathdb.service.vdi.service.datasets.getDatasetByID
import org.veupathdb.service.vdi.service.datasets.adminGetDatasetByID
import org.veupathdb.service.vdi.service.datasets.updateDatasetMeta
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated(allowGuests = false)
class VDIDatasetByIDEndpointsController(@Context request: ContainerRequest) : VdiDatasetsVdId, ControllerBase(request) {

  @AllowAdminAuth
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

  override fun deleteVdiDatasetsByVdId(vdID: String): VdiDatasetsVdId.DeleteVdiDatasetsByVdIdResponse {
    deleteDataset(userID.toUserID(), vdID.asVDIID())
    return VdiDatasetsVdId.DeleteVdiDatasetsByVdIdResponse.respond204()
  }
}