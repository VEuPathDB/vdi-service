package org.veupathdb.service.vdi.server.controllers

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsCommunity
import org.veupathdb.service.vdi.service.datasets.fetchCommunityUserDatasetList

@Authenticated(allowGuests = true)
class VDICommunityDatasetsController : VdiDatasetsCommunity {

  override fun getVdiDatasetsCommunity(): VdiDatasetsCommunity.GetVdiDatasetsCommunityResponse {
    return VdiDatasetsCommunity.GetVdiDatasetsCommunityResponse
      .respond200WithApplicationJson(fetchCommunityUserDatasetList())
  }
}