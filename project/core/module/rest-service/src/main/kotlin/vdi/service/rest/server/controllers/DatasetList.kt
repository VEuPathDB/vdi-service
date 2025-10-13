package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import kotlinx.coroutines.runBlocking
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.model.data.DatasetID
import vdi.core.db.cache.query.DatasetListQuery
import vdi.core.db.cache.model.DatasetOwnershipFilter
import vdi.logging.logger
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPostRequestBody
import vdi.service.rest.generated.resources.Datasets
import vdi.service.rest.server.inputs.cleanup
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.DatasetPostResponseBody
import vdi.service.rest.server.outputs.UnprocessableEntityError
import vdi.service.rest.server.services.dataset.createDataset
import vdi.service.rest.server.services.dataset.fetchUserDatasetList
import vdi.service.rest.util.ShortID

@Authenticated(allowGuests = false)
class DatasetList(@Context request: ContainerRequest, @Context val uploadConfig: UploadConfig)
  : Datasets
  , ControllerBase(request)
{
  private val log = logger()

  override fun getDatasets(projectId: String?, ownership: String?): Datasets.GetDatasetsResponse {
    // Parse the ownership filter field
    val parsedOwnership = ownership
      ?.let {
        DatasetOwnershipFilter.fromStringOrNull(it)
          ?: return Datasets.GetDatasetsResponse.respond400WithApplicationJson(BadRequestError("Invalid ownership query param value."))
      }
      ?: DatasetOwnershipFilter.ANY

    return Datasets.GetDatasetsResponse.respond200WithApplicationJson(
      fetchUserDatasetList(DatasetListQuery(userID, projectId, parsedOwnership))
    )
  }

  override fun postDatasets(entity: DatasetPostRequestBody?): Datasets.PostDatasetsResponse {
    with(entity ?: return Datasets.PostDatasetsResponse.respond400WithApplicationJson(BadRequestError("request body must not be empty"))) {
      cleanup()
      validate().let {
        if (it.isNotEmpty)
          return Datasets.PostDatasetsResponse.respond422WithApplicationJson(UnprocessableEntityError(it))
      }
    }

    // Generate a new dataset ID.
    val datasetID = runBlocking { DatasetID(ShortID.generate()) }

    log.info("issuing dataset ID {}", datasetID)

    return createDataset(datasetID, entity, uploadConfig)
  }
}

