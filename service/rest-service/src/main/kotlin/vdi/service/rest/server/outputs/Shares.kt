package vdi.service.rest.server.outputs

import vdi.service.generated.model.*
import vdi.service.rest.model.ShareFilterStatus
import vdi.service.rest.model.UserDetails
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction


internal fun ShareOffer(user: UserDetails, action: VDIShareOfferAction): vdi.service.rest.generated.model.ShareOffer =
  vdi.service.rest.generated.model.ShareOfferImpl().also {
    it.recipient = ShareOfferRecipient(user)
    it.status    = ShareOfferAction(action)
  }

fun ShareOfferAction(action: VDIShareOfferAction): vdi.service.rest.generated.model.ShareOfferAction =
  when (action) {
    VDIShareOfferAction.Grant  -> vdi.service.rest.generated.model.ShareOfferAction.GRANT
    VDIShareOfferAction.Revoke -> vdi.service.rest.generated.model.ShareOfferAction.REVOKE
  }

internal fun ShareOfferEntry(
  datasetID: DatasetID,
  shareStatus: ShareFilterStatus,
  datasetTypeName: DataType,
  datasetTypeVersion: String,
  datasetTypeDisplayName: String,
  owner:       UserDetails,
  projectIDs:  List<ProjectID>
): vdi.service.rest.generated.model.ShareOfferEntry =
  vdi.service.rest.generated.model.ShareOfferEntryImpl()
    .also {
      it.datasetId   = datasetID.toString()
      it.shareStatus = ShareOfferStatus(shareStatus)
      it.datasetType = DatasetTypeResponseBody(datasetTypeName, datasetTypeVersion, datasetTypeDisplayName)
      it.owner       = DatasetOwner(owner)
      it.projectIds  = projectIDs
    }

fun ShareOfferRecipient(user: UserDetails): vdi.service.rest.generated.model.ShareOfferRecipient =
  vdi.service.rest.generated.model.ShareOfferRecipientImpl().also {
    it.userId       = user.userID.toLong()
    it.firstName    = user.firstName
    it.lastName     = user.lastName
    it.email        = user.email
    it.organization = user.organization
  }

internal fun ShareOfferStatus(status: ShareFilterStatus): vdi.service.rest.generated.model.ShareOfferStatus =
  when (status) {
    ShareFilterStatus.Open     -> vdi.service.rest.generated.model.ShareOfferStatus.OPEN
    ShareFilterStatus.Accepted -> vdi.service.rest.generated.model.ShareOfferStatus.ACCEPTED
    ShareFilterStatus.Rejected -> vdi.service.rest.generated.model.ShareOfferStatus.REJECTED
    ShareFilterStatus.All      -> throw IllegalArgumentException("cannot convert ShareStatus.All to IO type")
  }
