package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import vdi.service.rest.generated.model.*
import vdi.service.rest.model.ShareFilterStatus
import vdi.service.rest.model.UserDetails


internal fun ShareOffer(user: UserDetails, action: VDIShareOfferAction): ShareOffer =
  ShareOfferImpl().also {
    it.recipient = ShareOfferRecipient(user)
    it.status    = ShareOfferAction(action)
  }

fun ShareOfferAction(action: VDIShareOfferAction): ShareOfferAction =
  when (action) {
    VDIShareOfferAction.Grant  -> ShareOfferAction.GRANT
    VDIShareOfferAction.Revoke -> ShareOfferAction.REVOKE
  }

internal fun ShareOfferEntry(
  datasetID: DatasetID,
  shareStatus: ShareFilterStatus,
  datasetTypeName: DataType,
  datasetTypeVersion: String,
  datasetTypeDisplayName: String,
  owner:       UserDetails,
  projectIDs:  List<ProjectID>
): ShareOfferEntry =
  ShareOfferEntryImpl()
    .also {
      it.datasetId   = datasetID.toString()
      it.shareStatus = ShareOfferStatus(shareStatus)
      it.datasetType = DatasetTypeOutput(datasetTypeName, datasetTypeVersion, datasetTypeDisplayName)
      it.owner       = DatasetOwner(owner)
      it.projectIds  = projectIDs
    }

fun ShareOfferRecipient(user: UserDetails): ShareOfferRecipient =
  ShareOfferRecipientImpl().also {
    it.userId       = user.userID.toLong()
    it.firstName    = user.firstName
    it.lastName     = user.lastName
    it.email        = user.email
    it.organization = user.organization
  }

internal fun ShareOfferStatus(status: ShareFilterStatus): ShareOfferStatus =
  when (status) {
    ShareFilterStatus.Open     -> ShareOfferStatus.OPEN
    ShareFilterStatus.Accepted -> ShareOfferStatus.ACCEPTED
    ShareFilterStatus.Rejected -> ShareOfferStatus.REJECTED
    ShareFilterStatus.All      -> throw IllegalArgumentException("cannot convert ShareStatus.All to IO type")
  }
