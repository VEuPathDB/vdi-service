package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.db.AccountDB
import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.util.defaultZone
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers

/**
 * Admin-auth endpoint for looking up a dataset by ID.  In this case we don't
 * return user information.
 */
fun adminGetDatasetByID(datasetID: DatasetID): DatasetDetails {
  val dataset = CacheDB.selectDataset(datasetID) ?: throw NotFoundException()

  val typeDisplayName = PluginHandlers[dataset.typeName, dataset.typeVersion]?.displayName
    ?: throw IllegalStateException("plugin missing: ${dataset.typeName}:${dataset.typeVersion}")

  // Lookup status information for the dataset
  val statuses = AppDB.getDatasetStatuses(datasetID, dataset.projects)

  val importMessages = CacheDB.selectImportMessages(datasetID)

  val metaJson = DatasetStore.getDatasetMeta(dataset.ownerID, datasetID)
    ?: throw IllegalStateException("meta.json missing from S3 for dataset ${dataset.ownerID}/$datasetID")

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
    out.dependencies   = metaJson.dependencies.map {
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
    CacheDB.selectSharesForDataset(datasetID)
  } else {
    emptyList()
  }

  // Lookup user details for the dataset's owner and any share users
  val userDetails = AccountDB.lookupUserDetails(HashSet<UserID>(shares.size + 1)
    .apply {
      add(userID)
      add(dataset.ownerID)
      shares.forEach { add(it.recipientID) }
    })

  // Lookup the display name for the plugin type for the dataset
  val typeDisplayName = PluginHandlers[dataset.typeName, dataset.typeVersion]?.displayName
    ?: throw IllegalStateException("plugin missing: ${dataset.typeName}:${dataset.typeVersion}")

  // Lookup status information for the dataset
  val statuses = AppDB.getDatasetStatuses(datasetID, dataset.projects)

  val importMessages = CacheDB.selectImportMessages(datasetID)

  val metaJson = DatasetStore.getDatasetMeta(dataset.ownerID, datasetID)
    ?: throw IllegalStateException("meta.json missing from S3 for dataset ${dataset.ownerID}/$datasetID")

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
    out.dependencies   = metaJson.dependencies.map {
      DatasetDependencyImpl().apply {
        resourceIdentifier = it.identifier
        resourceDisplayName = it.displayName
        resourceVersion = it.version
      }
    }

    shares.forEach { share ->
      out.shares.add(ShareOffer(
        userDetails[share.recipientID] ?: throw IllegalStateException("no user details for share recipient"),
        share.offerStatus
      ))
    }
  }
}

private fun requireDataset(userID: UserID, datasetID: DatasetID): DatasetRecord {
  var ds = CacheDB.selectDatasetForUser(userID, datasetID)

  if (ds == null) {
    ds = CacheDB.selectDataset(datasetID) ?: throw NotFoundException()

    if (ds.visibility == VDIDatasetVisibility.Private)
      throw NotFoundException()
  }

  return ds
}