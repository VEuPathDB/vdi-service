package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest
import org.veupathdb.service.vdi.generated.resources.VdiDatasets
import org.veupathdb.service.vdi.model.DatasetListQuery
import org.veupathdb.service.vdi.model.DatasetListSortField
import org.veupathdb.service.vdi.model.DatasetOwnershipFilter
import org.veupathdb.service.vdi.model.SortOrder
import org.veupathdb.service.vdi.service.datasets.list.fetchUserDatasetList
import java.util.*

private const val DEFAULT_OFFSET = 0
private const val DEFAULT_LIMIT  = 100
private const val MAX_LIMIT = 100

@Authenticated(allowGuests = false)
class VDIDatasetListEndpointController(@Context request: ContainerRequest) : VdiDatasets, ControllerBase(request) {

  override fun getVdiDatasets(
    projectId: String?,
    ownership: String?,
    offset: Int?,
    limit: Int?,
    sortField: String?,
    sortOrder: String?,
  ): VdiDatasets.GetVdiDatasetsResponse {
    // Get or default the offset value.
    val parsedOffset = offset ?: 0

    // Get or default the limit value.
    val parsedLimit  = limit ?: DEFAULT_LIMIT

    // Parse the ownership filter field
    val parsedOwnership = ownership
      ?.let {
        DatasetOwnershipFilter.fromStringOrNull(it)
          ?: throw BadRequestException("Invalid ownership query param value.")
      }
      ?: DatasetOwnershipFilter.ANY

    // Parse the sort field and sort order
    val parsedSortField: DatasetListSortField
    val parsedSortOrder: SortOrder

    if (sortField == null) {
      parsedSortField = DatasetListSortField.CREATION_TIMESTAMP
      parsedSortOrder = sortOrder
        ?.let {
          SortOrder.fromStringOrNull(it)
            ?: throw BadRequestException("invalid sort order query param value")
        }
        ?: SortOrder.DESCENDING
    } else {
      parsedSortField = DatasetListSortField.fromStringOrNull(sortField)
        ?: throw BadRequestException("invalid sort field query param value")
      parsedSortOrder = sortOrder
        ?.let {
          SortOrder.fromStringOrNull(it)
            ?: throw BadRequestException("invalid sort order query param value")
        }
        ?: SortOrder.ASCENDING
    }

    // Validate the offset and limit
    if (parsedOffset < 0)
      throw BadRequestException("Invalid offset value, must be greater than or equal to 0.")
    if (parsedLimit < 0 || parsedLimit > MAX_LIMIT)
      throw BadRequestException("Invalid limit value, must be greater than or equal to 0 and less than or equal to 100")

    return VdiDatasets.GetVdiDatasetsResponse.respond200WithApplicationJson(fetchUserDatasetList(DatasetListQuery(
      userID,
      projectId,
      parsedOwnership,
      parsedOffset,
      parsedLimit,
      parsedSortField,
      parsedSortOrder
    )))
  }

  override fun postVdiDatasets(entity: DatasetPostRequest): VdiDatasets.PostVdiDatasetsResponse {
    // validate the user
    // validate the request
    val datasetID = UUID.randomUUID()
      .toString()
      .replace("-", "")

    
    // write meta to database
    // upload dataset file to S3
    TODO("Not yet implemented")
  }
}

