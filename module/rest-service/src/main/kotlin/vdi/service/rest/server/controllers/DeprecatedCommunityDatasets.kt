package vdi.service.rest.server.controllers

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.generated.resources.VdiDatasetsCommunity
import vdi.service.rest.server.services.dataset.fetchCommunityUserDatasetList

@Deprecated("to be removed after client update")
@Authenticated(allowGuests = true)
class DeprecatedCommunityDatasets: VdiDatasetsCommunity {
  override fun getVdiDatasetsCommunity() =
    VdiDatasetsCommunity.GetVdiDatasetsCommunityResponse.respond200WithApplicationJson(fetchCommunityUserDatasetList())!!
}
