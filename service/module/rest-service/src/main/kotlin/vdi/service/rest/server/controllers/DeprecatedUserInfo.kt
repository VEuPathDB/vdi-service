package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.resources.VdiDatasetsShareOffers
import vdi.service.rest.generated.resources.VdiUsers
import vdi.service.rest.model.ShareFilterStatus
import vdi.service.rest.server.services.shares.lookupShares
import vdi.service.rest.server.services.users.getUserMetadata

@Deprecated("to be removed after client update")
@Authenticated(allowGuests = false)
class DeprecatedUserInfo(@Context request: ContainerRequest, @Context val uploadConfig: UploadConfig)
  : VdiUsers
  , VdiDatasetsShareOffers
  , ControllerBase(request)
{
  override fun getVdiUsersSelfMeta() =
    VdiUsers.GetVdiUsersSelfMetaResponse.respond200WithApplicationJson(getUserMetadata(uploadConfig))!!

  override fun getVdiUsersSelfShareOffers(status: String?) =
    VdiUsers.GetVdiUsersSelfShareOffersResponse
      .respond200WithApplicationJson(lookupShares(userID, status?.let(ShareFilterStatus::fromString) ?: ShareFilterStatus.Open))!!

  override fun getVdiDatasetsShareOffers(status: String?) =
    VdiDatasetsShareOffers.GetVdiDatasetsShareOffersResponse
      .respond200WithApplicationJson(lookupShares(userID, status?.let(ShareFilterStatus::fromString) ?: ShareFilterStatus.Open))!!
}
