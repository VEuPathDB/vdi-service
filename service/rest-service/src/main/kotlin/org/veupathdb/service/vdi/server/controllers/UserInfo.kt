package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.resources.Users
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsShareOffers
import org.veupathdb.service.vdi.generated.resources.VdiUsers
import org.veupathdb.service.vdi.model.ShareFilterStatus
import org.veupathdb.service.vdi.server.services.shares.lookupShares
import org.veupathdb.service.vdi.server.services.users.getUserMetadata
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated(allowGuests = false)
class UserInfo(@Context request: ContainerRequest)
  : Users
  , VdiUsers               // Deprecated API
  , VdiDatasetsShareOffers // Deprecated API
  , ControllerBase(request)
{
  override fun getUsersSelfMeta() =
    Users.GetUsersSelfMetaResponse.respond200WithApplicationJson(getUserMetadata(userID.toUserID()))!!

  override fun getUsersSelfShareOffers(status: String?) =
    Users.GetUsersSelfShareOffersResponse
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
    VdiUsers.GetVdiUsersSelfMetaResponse(usersSelfMeta)

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getUsersSelfShareOffers(status)"))
  override fun getVdiUsersSelfShareOffers(status: String?) =
    VdiUsers.GetVdiUsersSelfShareOffersResponse(getUsersSelfShareOffers(status))

  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getUsersSelfShareOffers(status)"))
  override fun getVdiDatasetsShareOffers(status: String?) =
    VdiDatasetsShareOffers.GetVdiDatasetsShareOffersResponse(getUsersSelfShareOffers(status))
}
