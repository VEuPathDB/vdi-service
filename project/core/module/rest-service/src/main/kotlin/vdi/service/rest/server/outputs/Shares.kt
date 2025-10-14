package vdi.service.rest.server.outputs

import vdi.model.data.DataType
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.DatasetShareOffer
import vdi.service.rest.generated.model.*
import vdi.service.rest.model.UserDetails


internal fun ShareOffer(user: UserDetails, action: DatasetShareOffer.Action): ShareOffer =
  ShareOfferImpl().also {
    it.recipient = ShareOfferRecipient(user)
    it.status    = ShareOfferAction(action)
  }

fun ShareOfferAction(action: DatasetShareOffer.Action): ShareOfferAction =
  when (action) {
    DatasetShareOffer.Action.Grant  -> ShareOfferAction.GRANT
    DatasetShareOffer.Action.Revoke -> ShareOfferAction.REVOKE
  }

internal fun ShareOfferEntry(
  datasetID: DatasetID,
  shareStatus: ShareOfferStatus,
  datasetTypeName: DataType,
  datasetTypeVersion: String,
  datasetTypeDisplayName: String,
  owner:       UserDetails,
  installTargets:  List<InstallTargetID>
): ShareOfferEntry =
  ShareOfferEntryImpl()
    .also {
      it.datasetId = datasetID.toString()
      it.shareStatus = shareStatus
      it.type = DatasetTypeOutput(datasetTypeName, datasetTypeVersion, datasetTypeDisplayName)
      it.owner = DatasetOwner(owner)
      it.installTargets = installTargets
    }

fun ShareOfferRecipient(user: UserDetails): ShareOfferRecipient =
  ShareOfferRecipientImpl().also {
    it.userId      = user.userID.toLong()
    it.firstName   = user.firstName
    it.lastName    = user.lastName
    it.email       = user.email
    it.affiliation = user.organization
  }
