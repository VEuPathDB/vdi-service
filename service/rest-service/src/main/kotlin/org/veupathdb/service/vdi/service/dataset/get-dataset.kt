package org.veupathdb.service.vdi.service.dataset

import jakarta.ws.rs.NotFoundException
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.genx.model.*
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.util.defaultZone
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetContact
import org.veupathdb.vdi.lib.common.model.VDIDatasetHyperlink
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.component.db.app.AppDB
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.plugin.mapping.PluginHandlers

/**
 * Admin-auth endpoint for looking up a dataset by ID.  In this case we don't
 * return user information.
 */
fun adminGetDatasetByID(datasetID: DatasetID): DatasetDetails {
  return DatasetDetailsImpl()
    .appendCommon(CacheDB().selectDataset(datasetID) ?: throw NotFoundException())
}

fun getDatasetByID(userID: UserID, datasetID: DatasetID): DatasetDetails {
  // Lookup dataset that is owned by or shared with the current user
  val dataset = requireDataset(userID, datasetID)

  if (dataset.isDeleted)
    throw NotFoundException()

  val shares = if (dataset.ownerID == userID) {
    CacheDB().selectSharesForDataset(datasetID)
  } else {
    emptyList()
  }

  // Lookup user details for the dataset's owner and any share users
  val idsIncludingShares = HashSet<UserID>(shares.size + 2)
    .apply {
      add(userID)
      add(dataset.ownerID)
      shares.forEach { add(it.recipientID) }
    }

  val userDetails = UserProvider.getUsersById(idsIncludingShares.map { it.toLong() })
    .asSequence()
    .map { (userIdLong, user) ->
      val userId = UserID(userIdLong)
      userId to UserDetails(userId, user.firstName, user.lastName, user.email, user.organization)
    }
    .toMap()

  // return the dataset
  return DatasetDetailsImpl().also { out ->
    out.owner            = DatasetOwner(userDetails[dataset.ownerID] ?: throw IllegalStateException("no user details for dataset owner"))
    out.shares           = ArrayList(shares.size)

    shares.forEach { share ->
      if (share.offerStatus != null)
        out.shares.add(ShareOffer(
          userDetails[share.recipientID] ?: throw IllegalStateException("no user details for share recipient"),
          share.offerStatus!!
        ))
    }
  }.appendCommon(dataset)
}

private fun requireDataset(userID: UserID, datasetID: DatasetID): DatasetRecord {
  var ds = CacheDB().selectDatasetForUser(userID, datasetID)

  if (ds == null) {
    ds = CacheDB().selectDataset(datasetID) ?: throw NotFoundException()

    if (ds.visibility == VDIDatasetVisibility.Private)
      throw NotFoundException()
  }

  return ds
}

/**
 * Appends the [DatasetDetails] fields that are common to both admin and user
 * lookups.
 *
 * @param src [DatasetRecord] instance fetched from the cache database.
 *
 * @return the receiver value.
 */
private fun DatasetDetails.appendCommon(src: DatasetRecord) = apply {
  val typeDisplayName = PluginHandlers[src.typeName, src.typeVersion]?.displayName
    // This means that the dataset is for a type that is no longer configured in
    // the service settings.
    //
    // This should never happen as anything more than a temporary case if/while
    // an app database is offline.
    ?: throw IllegalStateException("plugin missing: ${src.typeName}:${src.typeVersion}")

  // Lookup status information for the dataset
  val statuses = AppDB().getDatasetStatuses(src.datasetID, src.projects)

  // This value will be null if the async dataset upload has not yet completed.
  val metaJson = DatasetStore.getDatasetMeta(src.ownerID, src.datasetID)

  datasetId        = src.datasetID.toString()
  datasetType      = DatasetTypeInfo(src, typeDisplayName)
  name             = src.name
  shortName        = src.shortName
  shortAttribution = src.shortAttribution
  category         = src.category
  summary          = src.summary
  description      = src.description
  importMessages   = CacheDB().selectImportMessages(src.datasetID)
  origin           = src.origin
  status           = DatasetStatusInfo(src.importStatus, statuses)
  visibility       = DatasetVisibility(src.visibility)
  sourceUrl        = src.sourceURL
  projectIds       = src.projects.toList()
  created          = src.created.defaultZone()
  dependencies     = (metaJson?.dependencies ?: emptyList()).map {
    DatasetDependencyImpl().apply {
      resourceIdentifier = it.identifier
      resourceDisplayName = it.displayName
      resourceVersion = it.version
    }
  }
  publications     = (metaJson?.publications ?: emptyList()).map(VDIDatasetPublication::toExternal)
  hyperlinks       = (metaJson?.hyperlinks ?: emptyList()).map(VDIDatasetHyperlink::toExternal)
  contacts         = (metaJson?.contacts ?: emptyList()).map(VDIDatasetContact::toExternal)
  organisms        = metaJson?.organisms?.toList() ?: emptyList()
}
