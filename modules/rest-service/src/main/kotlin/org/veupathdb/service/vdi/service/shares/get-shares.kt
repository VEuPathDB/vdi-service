package org.veupathdb.service.vdi.service.shares

import org.veupathdb.service.vdi.db.AccountDB
import org.veupathdb.service.vdi.generated.model.ShareOfferEntry
import org.veupathdb.service.vdi.model.ShareFilterStatus
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetShareListEntry

/**
 * Looks up share information for the target recipient user where the share
 * record matches the given status.
 *
 * @param userID Share recipient user ID.
 *
 * @param status Target share status for which records should be returned.
 *
 * @return A list of zero or more [ShareOfferEntry] instance for the shares
 * matching the target filter.
 */
internal fun lookupShares(userID: UserID, status: ShareFilterStatus): List<ShareOfferEntry> =
  when (status) {
    ShareFilterStatus.Open     -> lookupOpenShares(userID)
    ShareFilterStatus.Accepted -> lookupAcceptedShares(userID)
    ShareFilterStatus.Rejected -> lookupRejectedShares(userID)
    ShareFilterStatus.All      -> lookupAllShares(userID)
  }

private fun lookupOpenShares(userID: UserID): List<ShareOfferEntry> =
  convertToOutType(CacheDB.selectOpenSharesForUser(userID))

private fun lookupAcceptedShares(userID: UserID): List<ShareOfferEntry> =
  convertToOutType(CacheDB.selectAcceptedSharesForUser(userID))

private fun lookupRejectedShares(userID: UserID): List<ShareOfferEntry> =
  convertToOutType(CacheDB.selectRejectedSharesForUser(userID))

private fun lookupAllShares(userID: UserID): List<ShareOfferEntry> =
  convertToOutType(CacheDB.selectAllSharesForUser(userID))

private fun convertToOutType(shares: Collection<DatasetShareListEntry>): List<ShareOfferEntry> {
  val ownerIDs = shares.asSequence()
    .map { it.ownerID }
    .toSet()

  val owners = AccountDB.lookupUserDetails(ownerIDs)

  return shares.map { ShareOfferEntry(
    datasetID          = it.datasetID,
    shareStatus        = ShareFilterStatus.Open,
    datasetTypeName    = it.typeName,
    datasetTypeVersion = it.typeVersion,
    owner              = owners[it.ownerID] ?: throw IllegalStateException("unknown dataset owner ${it.ownerID}"),
    projectIDs         = it.projects
  ) }
}