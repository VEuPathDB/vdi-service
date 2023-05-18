package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.model.DatasetShareOffer
import org.veupathdb.service.vdi.generated.model.DatasetShareReceipt
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsVdIdSharesRecipientUserId
import org.veupathdb.service.vdi.service.shares.putShareOffer
import org.veupathdb.service.vdi.service.shares.putShareReceipt
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated(allowGuests = false)
class VDIDatasetSharePutController(@Context request: ContainerRequest) : VdiDatasetsVdIdSharesRecipientUserId, ControllerBase(request) {

  override fun putVdiDatasetsSharesOfferByVdIdAndRecipientUserId(
    vdId: String,
    recipientUserId: Long,
    entity: DatasetShareOffer?,
  ): VdiDatasetsVdIdSharesRecipientUserId.PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse {
    if (entity == null)
      throw BadRequestException("body must not be blank or null")

    putShareOffer(vdId.asVDIID(), UserID(userID), UserID(recipientUserId), entity)

    return VdiDatasetsVdIdSharesRecipientUserId.PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse.respond204()
  }

  override fun putVdiDatasetsSharesReceiptByVdIdAndRecipientUserId(
    vdId: String,
    recipientUserId: Long,
    entity: DatasetShareReceipt?,
  ): VdiDatasetsVdIdSharesRecipientUserId.PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse {
    if (entity == null)
      throw BadRequestException("body must not be blank or null")

    if (userID != recipientUserId)
      throw ForbiddenException("cannot accept share offers for other users")

    putShareReceipt(vdId.asVDIID(), userID.toUserID(), entity)

    return VdiDatasetsVdIdSharesRecipientUserId.PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse.respond204()
  }
}