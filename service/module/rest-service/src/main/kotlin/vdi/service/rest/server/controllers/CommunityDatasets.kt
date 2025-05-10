package vdi.service.rest.server.controllers

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.generated.resources.DatasetsCommunity
import vdi.service.rest.generated.resources.DatasetsCommunity.GetDatasetsCommunityResponse
import vdi.service.rest.server.services.dataset.fetchCommunityUserDatasetList

@Authenticated(allowGuests = true)
class CommunityDatasets: DatasetsCommunity {
  override fun getDatasetsCommunity() =
    GetDatasetsCommunityResponse.respond200WithApplicationJson(fetchCommunityUserDatasetList())!!
}
