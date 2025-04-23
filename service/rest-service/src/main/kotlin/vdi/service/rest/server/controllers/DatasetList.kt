package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import kotlinx.coroutines.runBlocking
import org.glassfish.jersey.server.ContainerRequest
import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.generated.model.DatasetPostRequestBody
import vdi.service.rest.generated.resources.Datasets
import vdi.service.rest.generated.resources.VdiDatasets
import vdi.service.rest.server.inputs.cleanup
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.DatasetPostResponseBody
import vdi.service.rest.server.outputs.UnprocessableEntityError
import vdi.service.rest.server.services.dataset.createDataset
import vdi.service.rest.server.services.dataset.fetchUserDatasetList
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.lib.db.cache.model.DatasetListQuery
import vdi.lib.db.cache.model.DatasetOwnershipFilter

@Authenticated(allowGuests = false)
class DatasetList(@Context request: ContainerRequest)
  : Datasets
  , VdiDatasets // DEPRECATED API
  , ControllerBase(request)
{
  private val log = LoggerFactory.getLogger(javaClass)

  override fun getDatasets(projectId: String?, ownership: String?): Datasets.GetDatasetsResponse {
    // Parse the ownership filter field
    val parsedOwnership = ownership
      ?.let {
        DatasetOwnershipFilter.fromStringOrNull(it)
          ?: return Datasets.GetDatasetsResponse.respond400WithApplicationJson(BadRequestError("Invalid ownership query param value."))
      }
      ?: DatasetOwnershipFilter.ANY

    return Datasets.GetDatasetsResponse.respond200WithApplicationJson(
      fetchUserDatasetList(DatasetListQuery(userID, projectId, parsedOwnership), userID.toUserID())
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
    val datasetID = runBlocking { DatasetID() }

    log.info("issuing dataset ID {}", datasetID)

    // The dataset creation date may only be provided via the user-proxy admin
    // dataset upload endpoint.  Clear any value passed by the client on this
    // endpoint.
    entity.meta.createdOn = null

    createDataset(userID.toUserID(), datasetID, entity)

    return Datasets.PostDatasetsResponse
      .respond201WithApplicationJson(
        DatasetPostResponseBody(datasetID),
        Datasets.PostDatasetsResponse.headersFor201()
          .withLocation(request.absolutePath.toString().replaceAfterLast('/', datasetID.toString()))
      )
  }

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getDatasets(projectId, ownership)"))
  override fun getVdiDatasets(projectId: String?, ownership: String?) =
    VdiDatasets.GetVdiDatasetsResponse(getDatasets(projectId, ownership))

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("postDatasets(entity)"))
  override fun postVdiDatasets(entity: DatasetPostRequestBody?) =
    VdiDatasets.PostVdiDatasetsResponse(postDatasets(entity))
}

