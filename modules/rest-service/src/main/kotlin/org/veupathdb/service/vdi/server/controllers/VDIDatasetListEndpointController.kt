package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest
import org.veupathdb.service.vdi.generated.model.DatasetPostResponseImpl
import org.veupathdb.service.vdi.generated.model.validate
import org.veupathdb.service.vdi.generated.resources.VdiDatasets
import org.veupathdb.service.vdi.service.datasets.createDataset
import org.veupathdb.service.vdi.service.datasets.fetchUserDatasetList
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.toUserID
import org.veupathdb.vdi.lib.db.cache.model.DatasetListQuery
import org.veupathdb.vdi.lib.db.cache.model.DatasetListSortField
import org.veupathdb.vdi.lib.db.cache.model.DatasetOwnershipFilter
import org.veupathdb.vdi.lib.db.cache.model.SortOrder

private const val DEFAULT_OFFSET = 0
private const val DEFAULT_LIMIT  = 100
private const val MAX_LIMIT = 100

@Authenticated(allowGuests = false)
class VDIDatasetListEndpointController(@Context request: ContainerRequest) : VdiDatasets, ControllerBase(request) {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun getVdiDatasets(
    projectId: String?,
    ownership: String?,
    offset: Int?,
    limit: Int?,
    sortField: String?,
    sortOrder: String?,
  ): VdiDatasets.GetVdiDatasetsResponse {
    log.trace("getVdiDatasets(projectId={}, ownership={}, offset={}, limit={}, sortField={}, sortOrder={}", projectId, ownership, offset, limit, sortField, sortOrder)

    // Get or default the offset value.
    val parsedOffset = offset ?: DEFAULT_OFFSET

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

    // If no sorting field was provided
    if (sortField == null) {
      // Default the sorting field to the creation timestamp.
      parsedSortField = DatasetListSortField.CREATION_TIMESTAMP

      // If the sort order was provided, then use the user defined value,
      // otherwise, if no sort order was provided, fall back to "descending".
      parsedSortOrder = sortOrder
        ?.let {
          SortOrder.fromStringOrNull(it)
            ?: throw BadRequestException("invalid sort order query param value")
        }
        ?: SortOrder.DESCENDING
    }

    // else, if a sorting field _was_ provided
    else {
      // Use the user provided sorting field value.
      parsedSortField = DatasetListSortField.fromStringOrNull(sortField)
        ?: throw BadRequestException("invalid sort field query param value")

      // If the sort order was provided, then use the user defined value,
      // otherwise, if no sort order was provided, fall back to "ascending".
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

    return VdiDatasets.GetVdiDatasetsResponse.respond200WithApplicationJson(
      fetchUserDatasetList(
      DatasetListQuery(
        userID,
        projectId,
        parsedOwnership,
        parsedOffset,
        parsedLimit,
        parsedSortField,
        parsedSortOrder
      )
    )
    )
  }

  override fun postVdiDatasets(entity: DatasetPostRequest?): VdiDatasets.PostVdiDatasetsResponse {
    log.trace("postVdiDatasets(entity={})", entity)

    entity ?: throw BadRequestException()

    entity.validate()
      .throwIfNotEmpty()

    val datasetID = DatasetID()

    log.debug("issuing dataset ID {}", datasetID)

    createDataset(userID.toUserID(), datasetID, entity)

    return VdiDatasets.PostVdiDatasetsResponse
      .respond200WithApplicationJson(DatasetPostResponseImpl().apply { this.datasetID = datasetID.toString() })
  }
}

