package org.veupathdb.service.vdi.server.controllers

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.resources.DatasetsCommunity
import org.veupathdb.service.vdi.service.dataset.fetchCommunityUserDatasetList

@Authenticated(allowGuests = true)
class CommunityDatasets : DatasetsCommunity {

  override fun getDatasetsCommunity(): DatasetsCommunity.GetDatasetsCommunityResponse {
    return DatasetsCommunity.GetDatasetsCommunityResponse
      .respond200WithApplicationJson(fetchCommunityUserDatasetList())
  }
}
