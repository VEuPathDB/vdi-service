package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.resources.Users
import vdi.service.rest.model.ShareFilterStatus
import vdi.service.rest.server.services.shares.lookupShares
import vdi.service.rest.server.services.users.getUserMetadata

@Authenticated(allowGuests = false)
class UserInfo(@Context request: ContainerRequest, @Context val uploadConfig: UploadConfig)
  : Users
  , ControllerBase(request)
{
  override fun getUsersSelfMeta() =
    Users.GetUsersSelfMetaResponse.respond200WithApplicationJson(getUserMetadata(uploadConfig))!!

  override fun getUsersSelfShareOffers(status: String?) =
    Users.GetUsersSelfShareOffersResponse
      .respond200WithApplicationJson(lookupShares(userID, status?.let(ShareFilterStatus::fromString) ?: ShareFilterStatus.Open))!!
}
