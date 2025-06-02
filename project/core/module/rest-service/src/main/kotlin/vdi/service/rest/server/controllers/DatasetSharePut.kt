package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.service.rest.generated.model.DatasetShareOffer
import vdi.service.rest.generated.model.DatasetShareReceipt
import vdi.service.rest.generated.resources.DatasetsVdiIdSharesRecipientUserId
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.ForbiddenError
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.server.services.shares.adminPutShareOffer
import vdi.service.rest.server.services.shares.putShareOffer
import vdi.service.rest.server.services.shares.putShareReceipt
import vdi.service.rest.generated.resources.DatasetsVdiIdSharesRecipientUserId.PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse as PutReceipt

@Authenticated(adminOverride = ALLOW_ALWAYS)
class DatasetSharePut(@Context request: ContainerRequest)
  : DatasetsVdiIdSharesRecipientUserId
  , ControllerBase(request)
{
  override fun putDatasetsSharesOfferByVdiIdAndRecipientUserId(
    vdiId:           String,
    recipientUserId: Long?,
    entity:          DatasetShareOffer?,
  ) =
    entity?.let { when (maybeUser) {
      null -> adminPutShareOffer(DatasetID(vdiId), recipientUserId!!.toUserID(), it)
      else -> putShareOffer(DatasetID(vdiId), UserID(recipientUserId!!), it)
    } }
      ?: BadRequestError("body must not be blank or null").wrap()

  override fun putDatasetsSharesReceiptByVdiIdAndRecipientUserId(
    vdiId:           String,
    recipientUserId: Long?,
    entity:          DatasetShareReceipt?,
  ): PutReceipt {
    if (entity == null)
      return PutReceipt.respond400WithApplicationJson(BadRequestError("body must not be blank or null"))

    when (val userID = maybeUserID) {
      null -> putShareReceipt(DatasetID(vdiId), recipientUserId!!.toUserID(), entity)
      else -> {
        if (user.userId != recipientUserId)
          return PutReceipt.respond403WithApplicationJson(ForbiddenError("cannot accept share offers for other users"))

        putShareReceipt(DatasetID(vdiId), userID, entity)
      }
    }

    return PutReceipt.respond204()
  }
}
