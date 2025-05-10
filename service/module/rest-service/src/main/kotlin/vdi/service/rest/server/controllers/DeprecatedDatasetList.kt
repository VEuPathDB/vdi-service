package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPostRequestBody
import vdi.service.rest.generated.resources.VdiDatasets

@Deprecated("to be removed after client update")
@Authenticated(allowGuests = false)
class DeprecatedDatasetList(@Context request: ContainerRequest, @Context val uploadConfig: UploadConfig)
  : VdiDatasets // DEPRECATED API
  , ControllerBase(request)
{
  private val realApi = DatasetList(request, uploadConfig)

  override fun getVdiDatasets(projectId: String?, ownership: String?) =
    VdiDatasets.GetVdiDatasetsResponse(realApi.getDatasets(projectId, ownership))

  override fun postVdiDatasets(entity: DatasetPostRequestBody?) =
    VdiDatasets.PostVdiDatasetsResponse(realApi.postDatasets(entity))
}

