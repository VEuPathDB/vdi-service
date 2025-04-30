package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.generated.resources.Users
import vdi.service.rest.generated.resources.VdiDatasetsShareOffers
import vdi.service.rest.generated.resources.VdiUsers
import vdi.service.rest.model.ShareFilterStatus
import vdi.service.rest.server.services.shares.lookupShares
import vdi.service.rest.server.services.users.getUserMetadata

@Authenticated(allowGuests = false)
class UserInfo(@Context request: ContainerRequest)
  : Users
  , VdiUsers               // Deprecated API
  , VdiDatasetsShareOffers // Deprecated API
  , ControllerBase(request)
{
  override fun getUsersSelfMeta() =
    Users.GetUsersSelfMetaResponse.respond200WithApplicationJson(getUserMetadata(userID))!!

  override fun getUsersSelfShareOffers(status: String?) =
    Users.GetUsersSelfShareOffersResponse
      .respond200WithApplicationJson(lookupShares(userID, status?.let(ShareFilterStatus::fromString) ?: ShareFilterStatus.Open))!!

  //==========================================================================//
  // DEPRECATED API METHODS!!

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getUsersSelfMeta()"))
  override fun getVdiUsersSelfMeta() =
    VdiUsers.GetVdiUsersSelfMetaResponse(usersSelfMeta)

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getUsersSelfShareOffers(status)"))
  override fun getVdiUsersSelfShareOffers(status: String?) =
    VdiUsers.GetVdiUsersSelfShareOffersResponse(getUsersSelfShareOffers(status))

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getUsersSelfShareOffers(status)"))
  override fun getVdiDatasetsShareOffers(status: String?) =
    VdiDatasetsShareOffers.GetVdiDatasetsShareOffersResponse(getUsersSelfShareOffers(status))
}
