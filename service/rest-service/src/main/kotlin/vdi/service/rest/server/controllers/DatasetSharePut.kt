package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import vdi.service.rest.generated.model.DatasetShareOffer
import vdi.service.rest.generated.model.DatasetShareReceipt
import vdi.service.rest.generated.resources.DatasetsVdiIdSharesRecipientUserId
import vdi.service.rest.generated.resources.VdiDatasetsVdiIdSharesRecipientUserId
import vdi.service.rest.generated.resources.VdiDatasetsVdiIdSharesRecipientUserId.PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse
import vdi.service.rest.generated.resources.VdiDatasetsVdiIdSharesRecipientUserId.PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse
import vdi.service.rest.generated.resources.DatasetsVdiIdSharesRecipientUserId.PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse as PutOffer
import vdi.service.rest.generated.resources.DatasetsVdiIdSharesRecipientUserId.PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse as PutReceipt
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.ForbiddenError
import vdi.service.rest.server.services.shares.adminPutShareOffer
import vdi.service.rest.server.services.shares.putShareOffer
import vdi.service.rest.server.services.shares.putShareReceipt
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated
class DatasetSharePut(@Context request: ContainerRequest)
  : DatasetsVdiIdSharesRecipientUserId
  , VdiDatasetsVdiIdSharesRecipientUserId // DEPRECATED API
  , ControllerBase(request)
{
  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun putDatasetsSharesOfferByVdiIdAndRecipientUserId(
    vdiId: String,
    recipientUserId: Long,
    entity: DatasetShareOffer?,
  ): PutOffer {
    if (entity == null)
      return PutOffer.respond400WithApplicationJson(BadRequestError("body must not be blank or null"))

    val userID = maybeUserID?.toUserID()

    if (userID == null) {
      adminPutShareOffer(DatasetID(vdiId), recipientUserId.toUserID(), entity)
    } else {
      putShareOffer(DatasetID(vdiId), userID, UserID(recipientUserId), entity)
    }

    return PutOffer.respond204()
  }

  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun putDatasetsSharesReceiptByVdiIdAndRecipientUserId(
    vdiId: String,
    recipientUserId: Long,
    entity: DatasetShareReceipt?,
  ): PutReceipt {
    if (entity == null)
      return PutReceipt.respond400WithApplicationJson(BadRequestError("body must not be blank or null"))

    val userID = maybeUserID

    if (userID == null) {
      putShareReceipt(DatasetID(vdiId), recipientUserId.toUserID(), entity)
    } else {
      if (userID != recipientUserId)
        return PutReceipt.respond403WithApplicationJson(ForbiddenError("cannot accept share offers for other users"))

      putShareReceipt(DatasetID(vdiId), userID.toUserID(), entity)
    }

    return PutReceipt.respond204()
  }

  // DEPRECATED API
  @Authenticated(adminOverride = ALLOW_ALWAYS)
  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("putDatasetsSharesOfferByVdiIdAndRecipientUserId(vdiId, recipientUserId, entity)"))
  override fun putVdiDatasetsSharesOfferByVdiIdAndRecipientUserId(
    vdiId: String,
    recipientUserId: Long,
    entity: DatasetShareOffer?,
  ) = PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(putDatasetsSharesOfferByVdiIdAndRecipientUserId(vdiId, recipientUserId, entity))

  @Authenticated(adminOverride = ALLOW_ALWAYS)
  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("putDatasetsSharesReceiptByVdiIdAndRecipientUserId(vdiId, recipientUserId, entity)"))
  override fun putVdiDatasetsSharesReceiptByVdiIdAndRecipientUserId(
    vdiId: String,
    recipientUserId: Long,
    entity: DatasetShareReceipt?,
  ) = PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(putDatasetsSharesReceiptByVdiIdAndRecipientUserId(vdiId, recipientUserId, entity))
}
