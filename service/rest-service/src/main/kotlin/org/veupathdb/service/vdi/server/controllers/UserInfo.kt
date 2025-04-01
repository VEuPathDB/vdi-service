package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.resources.Users
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsShareOffers
import org.veupathdb.service.vdi.generated.resources.VdiUsers
import org.veupathdb.service.vdi.model.ShareFilterStatus
import org.veupathdb.service.vdi.service.shares.lookupShares
import org.veupathdb.service.vdi.service.users.getUserMetadata
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
      .respond200WithApplicationJson(lookupShares(
        userID.toUserID(),
        status?.let(ShareFilterStatus::fromString) ?: ShareFilterStatus.Open
      ))!!

  //==========================================================================//
  // DEPRECATED API METHODS!!

  override fun getVdiUsersSelfMeta() =
    VdiUsers.GetVdiUsersSelfMetaResponse.respond200WithApplicationJson(getUserMetadata(userID.toUserID()))!!

  override fun getVdiUsersSelfShareOffers(status: String?) =
    VdiUsers.GetVdiUsersSelfShareOffersResponse.respond200WithApplicationJson(lookupShares(
      userID.toUserID(),
      status?.let(ShareFilterStatus::fromString) ?: ShareFilterStatus.Open
    ))!!

  override fun getVdiDatasetsShareOffers(status: String?) =
    VdiDatasetsShareOffers.GetVdiDatasetsShareOffersResponse.respond200WithApplicationJson(lookupShares(
      userID.toUserID(),
      status?.let(ShareFilterStatus::fromString) ?: ShareFilterStatus.Open
    ))!!
}
