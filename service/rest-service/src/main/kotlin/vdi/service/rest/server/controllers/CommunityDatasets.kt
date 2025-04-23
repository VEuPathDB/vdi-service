package vdi.service.rest.server.controllers

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.generated.resources.DatasetsCommunity
import vdi.service.rest.generated.resources.VdiDatasetsCommunity
import vdi.service.server.services.dataset.fetchCommunityUserDatasetList

@Authenticated(allowGuests = true)
class CommunityDatasets
  : vdi.service.rest.generated.resources.DatasetsCommunity
  , vdi.service.rest.generated.resources.VdiDatasetsCommunity // DEPRECATED API
{
  override fun getDatasetsCommunity() =
    vdi.service.rest.generated.resources.DatasetsCommunity.GetDatasetsCommunityResponse
      .respond200WithApplicationJson(fetchCommunityUserDatasetList())!!

  // DEPRECATED API
  override fun getVdiDatasetsCommunity() =
    vdi.service.rest.generated.resources.VdiDatasetsCommunity.GetVdiDatasetsCommunityResponse(datasetsCommunity)
}
