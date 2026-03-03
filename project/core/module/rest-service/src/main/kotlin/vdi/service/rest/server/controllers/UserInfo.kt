package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.UsersSelfShareOffersGetStatus
import vdi.service.rest.generated.resources.Users
import vdi.service.rest.server.services.UserService

@Authenticated(allowGuests = false)
class UserInfo(
  @Context request: ContainerRequest,
  @param:Context val uploadConfig: UploadConfig,
)
  : Users
  , ControllerBase(request)
{
  override fun getUsersSelfMeta() =
    Users.GetUsersSelfMetaResponse.respond200WithApplicationJson(UserService.getUserMetadata(userID, uploadConfig))!!

  override fun getUsersSelfShareOffers(status: UsersSelfShareOffersGetStatus?): Users.GetUsersSelfShareOffersResponse =
    Users.GetUsersSelfShareOffersResponse
      .respond200WithApplicationJson(UserService.lookupShares(userID, status ?: UsersSelfShareOffersGetStatus.OPEN))!!
}
