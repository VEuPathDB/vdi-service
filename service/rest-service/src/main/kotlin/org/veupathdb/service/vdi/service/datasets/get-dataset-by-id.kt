package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.NotFoundException
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.util.defaultZone
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.component.db.app.AppDB
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.plugin.mapping.PluginHandlers

/**
 * Admin-auth endpoint for looking up a dataset by ID.  In this case we don't
 * return user information.
 */
fun adminGetDatasetByID(datasetID: DatasetID): DatasetDetails {
  val dataset = vdi.component.db.cache.CacheDB().selectDataset(datasetID) ?: throw NotFoundException()

  val typeDisplayName = PluginHandlers[dataset.typeName, dataset.typeVersion]?.displayName
    ?: throw IllegalStateException("plugin missing: ${dataset.typeName}:${dataset.typeVersion}")

  // Lookup status information for the dataset
  val statuses = AppDB().getDatasetStatuses(datasetID, dataset.projects)

  val importMessages = vdi.component.db.cache.CacheDB().selectImportMessages(datasetID)

  // This value will be null if the async dataset upload has not yet completed.
  val metaJson = DatasetStore.getDatasetMeta(dataset.ownerID, datasetID)

  return DatasetDetailsImpl().also { out ->
    out.datasetId      = datasetID.toString()
    out.datasetType    = DatasetTypeInfo(dataset, typeDisplayName)
    out.name           = dataset.name
    out.summary        = dataset.summary
    out.description    = dataset.description
    out.importMessages = importMessages
    out.origin         = dataset.origin
    out.status         = DatasetStatusInfo(dataset.importStatus, statuses)
    out.visibility     = DatasetVisibility(dataset.visibility)
    out.sourceUrl      = dataset.sourceURL
    out.projectIds     = dataset.projects.toList()
    out.created        = dataset.created.defaultZone()
    out.dependencies   = (metaJson?.dependencies ?: emptyList()).map {
      DatasetDependencyImpl().apply {
        resourceIdentifier = it.identifier
        resourceDisplayName = it.displayName
        resourceVersion = it.version
      }
    }
  }
}

fun getDatasetByID(userID: UserID, datasetID: DatasetID): DatasetDetails {
  // Lookup dataset that is owned by or shared with the current user
  val dataset = requireDataset(userID, datasetID)

  if (dataset.isDeleted)
    throw NotFoundException()

  val shares = if (dataset.ownerID == userID) {
    vdi.component.db.cache.CacheDB().selectSharesForDataset(datasetID)
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
  val userDetails = mutableMapOf<UserID, UserDetails>()
  UserProvider.getUsersById(idsIncludingShares.toList().map { it.toLong() })
    .map {
      val userId = UserID(it.key)
      val user = it.value
      val userDetail = UserDetails(userId, user.firstName, user.lastName, user.email, user.organization)
      userDetails.put(userId, userDetail)
    }

  // Lookup the display name for the plugin type for the dataset
  val typeDisplayName = PluginHandlers[dataset.typeName, dataset.typeVersion]?.displayName
    ?: throw IllegalStateException("plugin missing: ${dataset.typeName}:${dataset.typeVersion}")

  // Lookup status information for the dataset
  val statuses = AppDB().getDatasetStatuses(datasetID, dataset.projects)

  val importMessages = vdi.component.db.cache.CacheDB().selectImportMessages(datasetID)

  // This value will be null if the async dataset upload has not yet completed.
  val metaJson = DatasetStore.getDatasetMeta(dataset.ownerID, datasetID)

  // return the dataset
  return DatasetDetailsImpl().also { out ->
    out.datasetId      = datasetID.toString()
    out.owner          = DatasetOwner(userDetails[dataset.ownerID] ?: throw IllegalStateException("no user details for dataset owner"))
    out.datasetType    = DatasetTypeInfo(dataset, typeDisplayName)
    out.name           = dataset.name
    out.summary        = dataset.summary
    out.description    = dataset.description
    out.importMessages = importMessages
    out.origin         = dataset.origin
    out.status         = DatasetStatusInfo(dataset.importStatus, statuses)
    out.shares         = ArrayList(shares.size)
    out.visibility     = DatasetVisibility(dataset.visibility)
    out.sourceUrl      = dataset.sourceURL
    out.projectIds     = dataset.projects.toList()
    out.created        = dataset.created.defaultZone()
    out.dependencies   = (metaJson?.dependencies ?: emptyList()).map {
      DatasetDependencyImpl().apply {
        resourceIdentifier = it.identifier
        resourceDisplayName = it.displayName
        resourceVersion = it.version
      }
    }

    shares.forEach { share ->
      if (share.offerStatus != null)
        out.shares.add(ShareOffer(
          userDetails[share.recipientID] ?: throw IllegalStateException("no user details for share recipient"),
          share.offerStatus!!
        ))
    }
  }
}

private fun requireDataset(userID: UserID, datasetID: DatasetID): DatasetRecord {
  var ds = vdi.component.db.cache.CacheDB().selectDatasetForUser(userID, datasetID)

  if (ds == null) {
    ds = vdi.component.db.cache.CacheDB().selectDataset(datasetID) ?: throw NotFoundException()

    if (ds.visibility == VDIDatasetVisibility.Private)
      throw NotFoundException()
  }

  return ds
}