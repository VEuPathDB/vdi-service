package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.generated.resources.Users
import vdi.service.rest.generated.resources.VdiDatasetsShareOffers
import vdi.service.rest.generated.resources.VdiUsers
import vdi.service.model.ShareFilterStatus
import vdi.service.server.services.shares.lookupShares
import vdi.service.server.services.users.getUserMetadata
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated(allowGuests = false)
class UserInfo(@Context request: ContainerRequest)
  : vdi.service.rest.generated.resources.Users
  , vdi.service.rest.generated.resources.VdiUsers               // Deprecated API
  , vdi.service.rest.generated.resources.VdiDatasetsShareOffers // Deprecated API
  , ControllerBase(request)
{
  override fun getUsersSelfMeta() =
    vdi.service.rest.generated.resources.Users.GetUsersSelfMetaResponse.respond200WithApplicationJson(getUserMetadata(userID.toUserID()))!!

  override fun getUsersSelfShareOffers(status: String?) =
    vdi.service.rest.generated.resources.Users.GetUsersSelfShareOffersResponse
      .respond200WithApplicationJson(
        lookupShares(
        userID.toUserID(),
        status?.let(ShareFilterStatus::fromString) ?: ShareFilterStatus.Open
      )
      )!!

  //==========================================================================//
  // DEPRECATED API METHODS!!

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getUsersSelfMeta()"))
  override fun getVdiUsersSelfMeta() =
    vdi.service.rest.generated.resources.VdiUsers.GetVdiUsersSelfMetaResponse(usersSelfMeta)

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getUsersSelfShareOffers(status)"))
  override fun getVdiUsersSelfShareOffers(status: String?) =
    vdi.service.rest.generated.resources.VdiUsers.GetVdiUsersSelfShareOffersResponse(getUsersSelfShareOffers(status))

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getUsersSelfShareOffers(status)"))
  override fun getVdiDatasetsShareOffers(status: String?) =
    vdi.service.rest.generated.resources.VdiDatasetsShareOffers.GetVdiDatasetsShareOffersResponse(getUsersSelfShareOffers(status))
}
