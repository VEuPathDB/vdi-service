package org.veupathdb.service.vdi.service.shares

import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import org.veupathdb.service.vdi.generated.model.ShareOfferEntry
import org.veupathdb.service.vdi.model.ShareFilterStatus
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.model.DatasetShareListEntry
import vdi.component.plugin.mapping.PluginHandlers

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
  convertToOutType(vdi.component.db.cache.CacheDB().selectOpenSharesForUser(userID))

private fun lookupAcceptedShares(userID: UserID): List<ShareOfferEntry> =
  convertToOutType(vdi.component.db.cache.CacheDB().selectAcceptedSharesForUser(userID))

private fun lookupRejectedShares(userID: UserID): List<ShareOfferEntry> =
  convertToOutType(vdi.component.db.cache.CacheDB().selectRejectedSharesForUser(userID))

private fun lookupAllShares(userID: UserID): List<ShareOfferEntry> =
  convertToOutType(vdi.component.db.cache.CacheDB().selectAllSharesForUser(userID))

private fun convertToOutType(shares: Collection<DatasetShareListEntry>): List<ShareOfferEntry> {
  val ownerIDs = shares.asSequence()
    .map { it.ownerID }
    .toSet()

  val owners= UserProvider.getUsersById(ownerIDs.map { it.toLong() })
    .asSequence()
    .map { (userIdLong, user) ->
      val userId = UserID(userIdLong)
      userId to UserDetails(userId, user.firstName, user.lastName, user.email, user.organization)
    }
    .toMap()

  return shares.map {
    val typeDisplayName = PluginHandlers[it.typeName, it.typeVersion]?.displayName
      ?: throw IllegalStateException("unregistered dataset type: ${it.typeName}:${it.typeVersion}")

    ShareOfferEntry(
      datasetID              = it.datasetID,
      shareStatus            = ShareFilterStatus.Open,
      datasetTypeName        = it.typeName.lowercase(),
      datasetTypeVersion     = it.typeVersion,
      datasetTypeDisplayName = typeDisplayName,
      owner                  = owners[it.ownerID] ?: throw IllegalStateException("unknown dataset owner ${it.ownerID}"),
      projectIDs             = it.projects
    )
  }
}