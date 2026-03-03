package vdi.service.rest.server.controllers;

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import vdi.service.rest.generated.resources.DatasetsCommunity;
import vdi.service.rest.server.services.dataset.DatasetListService;

@Authenticated(allowGuests = true)
public class CommunityDatasetController implements DatasetsCommunity {
  @Override
  public GetDatasetsCommunityResponse getDatasetsCommunity() {
    return GetDatasetsCommunityResponse.respond200WithApplicationJson(DatasetListService.fetchCommunityUserDatasetList());
  }
}
