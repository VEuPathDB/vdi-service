package org.veupathdb.service.vdi.service.dataset

import jakarta.ws.rs.NotFoundException
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.genx.model.*
import org.veupathdb.service.vdi.server.outputs.toExternal
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.server.outputs.*
import org.veupathdb.service.vdi.server.outputs.DatasetDependencies
import org.veupathdb.service.vdi.server.outputs.DatasetDetails
import org.veupathdb.service.vdi.server.outputs.DatasetTypeResponseBody
import org.veupathdb.service.vdi.server.outputs.toExternal
import org.veupathdb.service.vdi.util.defaultZone
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.component.db.app.AppDB
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.db.cache.model.DatasetShare
import vdi.lib.plugin.mapping.PluginHandlers

/**
 * Admin-auth endpoint for looking up a dataset by ID.  In this case we don't
 * return user information.
 */
fun adminGetDatasetByID(datasetID: DatasetID): DatasetDetails =
  getDatasetByID(null, datasetID, true)

fun getDatasetByID(userID: UserID?, datasetID: DatasetID, includeDeleted: Boolean = false): DatasetDetails {
  // Lookup dataset that is owned by or shared with the current user
  val dataset = if (userID == null)
    CacheDB().selectDataset(datasetID) ?: throw NotFoundException()
  else
    requireDataset(userID, datasetID)

  if (!includeDeleted && userID != null && dataset.isDeleted)
    throw NotFoundException()

  val shares = if (dataset.ownerID == userID) {
    CacheDB().selectSharesForDataset(datasetID)
  } else {
    emptyList()
  }

  val userDetails = getUserDetails(dataset.ownerID, shares, userID)

  val typeDisplayName = PluginHandlers[dataset.typeName, dataset.typeVersion]?.displayName
  // This means that the dataset is for a type that is no longer configured in
  // the service settings.
  //
  // This should never happen as anything more than a temporary case if/while
  // an app database is offline.
    ?: throw IllegalStateException("plugin missing: ${dataset.typeName}:${dataset.typeVersion}")

  val metaJson = DatasetStore.getDatasetMeta(dataset.ownerID, dataset.datasetID)

  val revisions = CacheDB().selectRevisions(datasetID)

  val revisionIndex = metaJson?.revisionHistory?.associateBy { it.revisionID }
    ?: emptyMap()

  return DatasetDetails(
    datasetID = datasetID,
    owner = DatasetOwner(userDetails.requireDetails(dataset.ownerID)),
    datasetType = DatasetTypeResponseBody(dataset, typeDisplayName),
    name = dataset.name,
    origin = dataset.origin,
    projectIDs = dataset.projects,
    visibility = org.veupathdb.service.vdi.server.inputs.DatasetVisibility(dataset.visibility),
    status = DatasetStatusInfo(dataset.importStatus, AppDB().getDatasetStatuses(dataset.datasetID, dataset.projects)),
    created = dataset.created.defaultZone(),
    dependencies = metaJson?.dependencies?.let(::DatasetDependencies) ?: emptyList(),
    shortName = dataset.shortName,
    shortAttribution = dataset.shortAttribution,
    category = dataset.category,
    summary = dataset.summary,
    description = dataset.description,
    sourceURL = dataset.sourceURL,
    importMessages = CacheDB().selectImportMessages(datasetID),
    shares = shares.asSequence()
      .map { s -> s.offerStatus?.let { ShareOffer(userDetails.requireDetails(s.recipientID), it) } }
      .filterNotNull()
      .toList(),
    publications = metaJson?.publications?.map(VDIDatasetPublication::toExternal),
    hyperlinks = metaJson?.hyperlinks?.map(::toExternal),
    organisms = metaJson?.organisms?.toList(),
    contacts = metaJson?.contacts?.map(::toExternal),
    originalID = revisions?.originalID,
    revisionHistory = revisions?.records?.map { it.toExternal(revisionIndex[it.revisionID]?.revisionNote) }
  )
}

internal fun getLatestRevision(datasetID: DatasetID) =
  CacheDB().selectLatestRevision(datasetID)?.revisionID

private fun requireDataset(userID: UserID, datasetID: DatasetID): DatasetRecord {
  var ds = CacheDB().selectDatasetForUser(userID, datasetID)

  if (ds == null) {
    ds = CacheDB().selectDataset(datasetID) ?: throw NotFoundException()

    if (ds.visibility == VDIDatasetVisibility.Private)
      throw NotFoundException()
  }

  return ds
}

private fun getUserDetails(ownerID: UserID, shares: Collection<DatasetShare>, requesterID: UserID? = null): Map<UserID, UserDetails> {
  // Lookup user details for the dataset's owner and any share users
  val idsIncludingShares = HashSet<UserID>(shares.size + 2)
    .apply {
      if (requesterID != null)
        add(requesterID)
      add(ownerID)
      shares.forEach { add(it.recipientID) }
    }

  return UserProvider.getUsersById(idsIncludingShares.map { it.toLong() })
    .asSequence()
    .map { (userIdLong, user) ->
      val userId = UserID(userIdLong)
      userId to UserDetails(userId, user.firstName, user.lastName, user.email, user.organization)
    }
    .toMap()
}
