package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.generated.model.DatasetPutRequestBody
import vdi.service.rest.generated.resources.VdiDatasetsVdiId

@Authenticated
@Deprecated("to be removed after client update")
class DeprecatedDatasetByID(@Context request: ContainerRequest, @Context val uploadConfig: UploadConfig)
  : VdiDatasetsVdiId
  , ControllerBase(request)
{
  private val realApi = DatasetByID(request, uploadConfig)
  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun getVdiDatasetsByVdiId(vdiId: String) =
    VdiDatasetsVdiId.GetVdiDatasetsByVdiIdResponse(realApi.getDatasetsByVdiId(vdiId))

  override fun patchVdiDatasetsByVdiId(vdiId: String, entity: DatasetPatchRequestBody?) =
    VdiDatasetsVdiId.PatchVdiDatasetsByVdiIdResponse(realApi.patchDatasetsByVdiId(vdiId, entity))

  override fun putVdiDatasetsByVdiId(vdiId: String, entity: DatasetPutRequestBody?) =
    VdiDatasetsVdiId.PutVdiDatasetsByVdiIdResponse(realApi.putDatasetsByVdiId(vdiId, entity))

  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun deleteVdiDatasetsByVdiId(vdiId: String) =
    VdiDatasetsVdiId.DeleteVdiDatasetsByVdiIdResponse(realApi.deleteDatasetsByVdiId(vdiId))
}
