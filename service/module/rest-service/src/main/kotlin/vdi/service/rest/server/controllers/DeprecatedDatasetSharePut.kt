package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import vdi.service.rest.generated.model.DatasetShareOffer
import vdi.service.rest.generated.model.DatasetShareReceipt
import vdi.service.rest.generated.resources.VdiDatasetsVdiIdSharesRecipientUserId
import vdi.service.rest.generated.resources.VdiDatasetsVdiIdSharesRecipientUserId.PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse
import vdi.service.rest.generated.resources.VdiDatasetsVdiIdSharesRecipientUserId.PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse

@Deprecated("to be removed after client update")
@Authenticated(adminOverride = ALLOW_ALWAYS)
class DeprecatedDatasetSharePut(@Context request: ContainerRequest)
  : VdiDatasetsVdiIdSharesRecipientUserId // DEPRECATED API
  , ControllerBase(request)
{
  private val realApi = DatasetSharePut(request)

  override fun putVdiDatasetsSharesOfferByVdiIdAndRecipientUserId(
    vdiId: String,
    recipientUserId: Long?,
    entity: DatasetShareOffer?,
  ) = PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(realApi.putDatasetsSharesOfferByVdiIdAndRecipientUserId(vdiId, recipientUserId, entity))

  override fun putVdiDatasetsSharesReceiptByVdiIdAndRecipientUserId(
    vdiId: String,
    recipientUserId: Long?,
    entity: DatasetShareReceipt?,
  ) = PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(realApi.putDatasetsSharesReceiptByVdiIdAndRecipientUserId(vdiId, recipientUserId, entity))
}
