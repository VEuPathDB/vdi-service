package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import kotlinx.coroutines.runBlocking
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.core.db.cache.model.DatasetOwnershipFilter
import vdi.core.db.cache.query.DatasetListQuery
import vdi.logging.mark
import vdi.model.meta.DatasetID
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPostRequestBody
import vdi.service.rest.generated.resources.Datasets
import vdi.service.rest.server.inputs.cleanup
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.UnprocessableEntityError
import vdi.service.rest.server.services.dataset.createDataset
import vdi.service.rest.server.services.dataset.fetchUserDatasetList
import vdi.service.rest.util.ShortID
import vdi.util.fn.fold
import vdi.service.rest.generated.resources.Datasets.GetDatasetsResponse as GetResponse
import vdi.service.rest.generated.resources.Datasets.PostDatasetsResponse as PostResponse

@Authenticated(allowGuests = false)
class DatasetList(
  @Context request: ContainerRequest,
  @param:Context val uploadConfig: UploadConfig,
)
  : Datasets
  , ControllerBase(request)
{
  override fun getDatasets(projectId: String?, ownership: String?): GetResponse {
    // Parse the ownership filter field
    val parsedOwnership = ownership
      ?.let {
        DatasetOwnershipFilter.fromStringOrNull(it)
          ?: return GetResponse.respond400WithApplicationJson(BadRequestError("Invalid ownership query param value."))
      }
      ?: DatasetOwnershipFilter.ANY

    return GetResponse.respond200WithApplicationJson(
      fetchUserDatasetList(DatasetListQuery(userID, projectId, parsedOwnership))
    )
  }

  override fun postDatasets(entity: DatasetPostRequestBody?): PostResponse {
    with(entity ?: return PostResponse.respond400WithApplicationJson(BadRequestError("request body must not be empty"))) {
      cleanup()
      validate().fold(
        PostResponse::respond400WithApplicationJson,
        { v -> v.takeIf { it.isNotEmpty }
          ?.let(::UnprocessableEntityError)
          ?.let(PostResponse::respond422WithApplicationJson) }
      )?.also { return it }
    }

    // Generate a new dataset ID.
    val datasetID = runBlocking { DatasetID(ShortID.generate()) }

    logger.info("issuing dataset ID {}", datasetID)
    logger = logger.mark(datasetID = datasetID)

    return createDataset(datasetID, entity, uploadConfig)
  }
}

