package vdi.service.rest.server;

import jakarta.ws.rs.core.Context;
import org.glassfish.jersey.server.ContainerRequest;
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import vdi.service.rest.config.UploadConfig;
import vdi.service.rest.generated.model.UsersSelfShareOffersGetStatus;
import vdi.service.rest.generated.resources.Users;
import vdi.service.rest.server.services.shares.DatasetShareService;
import vdi.service.rest.services.UserMetaService;

@Authenticated
public class UserInfoController extends AbstractController implements Users {
  private final UploadConfig uploadConfig;

  public UserInfoController(
    @Context ContainerRequest request,
    @Context UploadConfig uploadConfig
  ) {
    super(request);
    this.uploadConfig = uploadConfig;
  }

  @Override
  public GetUsersSelfMetaResponse getUsersSelfMeta() {
    return GetUsersSelfMetaResponse.respond200WithApplicationJson(
      UserMetaService.getUserMetadata(getUserId(), uploadConfig)
    );
  }

  @Override
  public GetUsersSelfShareOffersResponse getUsersSelfShareOffers(UsersSelfShareOffersGetStatus status) {
    return GetUsersSelfShareOffersResponse.respond200WithApplicationJson(
      DatasetShareService.lookupShares(
        getUserId(),
        status == null ? UsersSelfShareOffersGetStatus.OPEN : status
      )
    );
  }
}
