package vdi.service.rest.server.controllers

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.generated.resources.DatasetsCommunity
import vdi.service.rest.generated.resources.VdiDatasetsCommunity
import vdi.service.rest.server.services.dataset.fetchCommunityUserDatasetList

@Authenticated(allowGuests = true)
class CommunityDatasets
  : DatasetsCommunity
  , VdiDatasetsCommunity // DEPRECATED API
{
  override fun getDatasetsCommunity() =
    DatasetsCommunity.GetDatasetsCommunityResponse
      .respond200WithApplicationJson(fetchCommunityUserDatasetList())!!

  // DEPRECATED API
  override fun getVdiDatasetsCommunity() =
    VdiDatasetsCommunity.GetVdiDatasetsCommunityResponse(datasetsCommunity)
}
