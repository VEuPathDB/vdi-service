package org.veupathdb.service.vdi.server.controllers

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.resources.DatasetsCommunity
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsCommunity
import org.veupathdb.service.vdi.server.services.dataset.fetchCommunityUserDatasetList

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
