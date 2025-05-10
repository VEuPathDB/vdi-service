package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsShareOffers
import org.veupathdb.service.vdi.model.ShareFilterStatus
import org.veupathdb.service.vdi.service.shares.lookupShares
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated(allowGuests = false)
class DatasetShareList(@Context request: ContainerRequest)
  : VdiDatasetsShareOffers
  , ControllerBase(request)
{
  override fun getVdiDatasetsShareOffers(status: String?): VdiDatasetsShareOffers.GetVdiDatasetsShareOffersResponse =
    VdiDatasetsShareOffers.GetVdiDatasetsShareOffersResponse
      .respond200WithApplicationJson(lookupShares(
        userID.toUserID(),
        status?.let(ShareFilterStatus::fromString) ?: ShareFilterStatus.Open
      ))
}
