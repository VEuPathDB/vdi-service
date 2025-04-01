package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.model.DatasetPostRequestBody
import org.veupathdb.service.vdi.generated.resources.Datasets
import org.veupathdb.service.vdi.generated.resources.VdiDatasets
import org.veupathdb.service.vdi.generated.resources.VdiDatasets.*
import org.veupathdb.service.vdi.genx.model.*
import org.veupathdb.service.vdi.service.dataset.createDataset
import org.veupathdb.service.vdi.service.dataset.fetchUserDatasetList
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.component.db.cache.model.DatasetListQuery
import vdi.component.db.cache.model.DatasetOwnershipFilter

@Authenticated(allowGuests = false)
class DatasetList(@Context request: ContainerRequest)
  : Datasets
  , VdiDatasets // DEPRECATED API
  , ControllerBase(request)
{
  private val log = LoggerFactory.getLogger(javaClass)

  override fun getVdiDatasets(projectId: String?, ownership: String?): GetVdiDatasetsResponse {
    // Parse the ownership filter field
    val parsedOwnership = ownership
      ?.let {
        DatasetOwnershipFilter.fromStringOrNull(it)
          ?: return GetVdiDatasetsResponse.respond400WithApplicationJson(BadRequestError("Invalid ownership query param value."))
      }
      ?: DatasetOwnershipFilter.ANY

    return GetVdiDatasetsResponse.respond200WithApplicationJson(
      fetchUserDatasetList(DatasetListQuery(userID, projectId, parsedOwnership), userID.toUserID())
    )
  }

  override fun postVdiDatasets(entity: DatasetPostRequestBody?): PostVdiDatasetsResponse {
    // Require and validate the request body.
    with(entity ?: return PostVdiDatasetsResponse.respond400WithApplicationJson(BadRequestError("request body must not be empty"))) {
      cleanup()
      validate().let {
        if (it.isNotEmpty)
          return PostVdiDatasetsResponse.respond422WithApplicationJson(UnprocessableEntityError(it))
      }
    }

    // Generate a new dataset ID.
    val datasetID = DatasetID()

    log.info("issuing dataset ID {}", datasetID)

    // The dataset creation date may only be provided via the user-proxy admin
    // dataset upload endpoint.  Clear any value passed by the client on this
    // endpoint.
    entity.meta.createdOn = null

    createDataset(userID.toUserID(), datasetID, entity)

    return PostVdiDatasetsResponse
      .respond201WithApplicationJson(
        DatasetPostResponseBody(datasetID),
        PostVdiDatasetsResponse.headersFor201()
          .withLocation(request.absolutePath.toString().replaceAfterLast('/', datasetID.toString()))
      )
  }
}

