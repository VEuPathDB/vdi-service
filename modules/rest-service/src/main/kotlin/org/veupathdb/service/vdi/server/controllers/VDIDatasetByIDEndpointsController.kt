package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsVdId
import org.veupathdb.service.vdi.service.datasets.getDatasetByID
import vdi.components.common.fields.asWDKUserID

class VDIDatasetByIDEndpointsController(@Context request: ContainerRequest) : VdiDatasetsVdId, ControllerBase(request) {

  override fun getVdiDatasetsByVdId(vdID: String): VdiDatasetsVdId.GetVdiDatasetsByVdIdResponse =
    VdiDatasetsVdId.GetVdiDatasetsByVdIdResponse
      .respond200WithApplicationJson(getDatasetByID(userID.asWDKUserID(), vdID.asVDIID()))

  override fun patchVdiDatasetsByVdId(
    vdID: String,
    entity: DatasetPatchRequest,
  ): VdiDatasetsVdId.PatchVdiDatasetsByVdIdResponse {
    TODO("Not yet implemented")
  }

  override fun deleteVdiDatasetsByVdId(vdID: String): VdiDatasetsVdId.DeleteVdiDatasetsByVdIdResponse {
    TODO("Not yet implemented")
  }
}