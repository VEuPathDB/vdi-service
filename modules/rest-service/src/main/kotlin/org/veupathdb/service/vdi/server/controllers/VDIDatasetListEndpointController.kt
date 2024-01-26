package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest
import org.veupathdb.service.vdi.generated.model.DatasetPostResponse
import org.veupathdb.service.vdi.generated.model.validate
import org.veupathdb.service.vdi.generated.resources.VdiDatasets
import org.veupathdb.service.vdi.service.datasets.createDataset
import org.veupathdb.service.vdi.service.datasets.fetchUserDatasetList
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.toUserID
import org.veupathdb.vdi.lib.db.cache.model.DatasetListQuery
import org.veupathdb.vdi.lib.db.cache.model.DatasetOwnershipFilter

@Authenticated(allowGuests = false)
class VDIDatasetListEndpointController(@Context request: ContainerRequest) : VdiDatasets, ControllerBase(request) {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun getVdiDatasets(
    projectId: String?,
    ownership: String?,
  ): VdiDatasets.GetVdiDatasetsResponse {
    log.trace("getVdiDatasets(projectId={}, ownership={}", projectId, ownership)

    // Parse the ownership filter field
    val parsedOwnership = ownership
      ?.let {
        DatasetOwnershipFilter.fromStringOrNull(it)
          ?: throw BadRequestException("Invalid ownership query param value.")
      }
      ?: DatasetOwnershipFilter.ANY

    return VdiDatasets.GetVdiDatasetsResponse.respond200WithApplicationJson(
      fetchUserDatasetList(DatasetListQuery(userID, projectId, parsedOwnership))
    )
  }

  override fun postVdiDatasets(entity: DatasetPostRequest?): VdiDatasets.PostVdiDatasetsResponse {
    log.trace("postVdiDatasets(entity={})", entity)

    // Require and validate the request body.
    (entity ?: throw BadRequestException("request body must not be empty"))
      .validate()
      .throwIfNotEmpty()

    // Generate a new dataset ID.
    val datasetID = DatasetID()

    log.info("issuing dataset ID {}", datasetID)

    createDataset(userID.toUserID(), datasetID, entity)

    return VdiDatasets.PostVdiDatasetsResponse
      .respond200WithApplicationJson(DatasetPostResponse(datasetID))
  }
}

