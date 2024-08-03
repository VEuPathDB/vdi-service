package org.veupathdb.service.vdi.service.datasets

import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.service.vdi.util.defaultZone
import org.veupathdb.service.vdi.util.reduceTo
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import vdi.component.db.app.AppDB
import vdi.component.db.app.model.InstallStatuses
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetFileSummary
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.model.DatasetListQuery
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.plugin.mapping.PluginHandlers
import org.veupathdb.lib.container.jaxrs.providers.UserProvider

fun fetchUserDatasetList(query: DatasetListQuery, userID: UserID): List<DatasetListEntry> {
  return fetchDatasetList(CacheDB().selectDatasetList(query), userID)
}

fun fetchCommunityUserDatasetList(): List<DatasetListEntry> {
  return fetchDatasetList(CacheDB().selectNonPrivateDatasets(), null)
}

private fun fetchDatasetList(datasetList: List<DatasetRecord>, requesterID: UserID?): List<DatasetListEntry> {
  val cacheDB = CacheDB()

  val datasetIDs = datasetList.map(DatasetRecord::datasetID)

  // Get the shares data for the list of results.
  val shares = cacheDB.selectSharesForDatasets(datasetIDs)
  val shareCount = shares.reduceTo(0) { _, _, v -> v.size }

  // build a set for collecting user IDs to use when querying for user details
  val userIDs = HashSet<UserID>(shareCount + datasetList.size)

  // Append share recipient user IDs to the user IDs array
  shares.forEach { (_, v) -> v.asSequence()
    .filter { it.offerStatus == VDIShareOfferAction.Grant }
    .forEach { userIDs.add(it.recipientID) }}

  // build a map for collecting project ID -> dataset ID collection mappings
  // to use when querying for dataset status info.
  val projectToDatasetID = HashMap<String, MutableSet<DatasetID>>(12)

  // Get file counts and sizes for the datasets.
  val fileSummaries = cacheDB.selectUploadFileSummaries(datasetIDs)

  // for each dataset the original search returned
  datasetList.forEach { ds ->

    // add the owner id to the set of user ids
    userIDs.add(ds.ownerID)

    // for each project the dataset is associated with
    ds.projects.forEach { project ->
      // add an entry into the projectToDatasetID map for the combination of
      // project id and dataset id
      projectToDatasetID.computeIfAbsent(project) { HashSet(1) }
        .add(ds.datasetID)
    }
  }

  // Get the statuses for each installation by project ID and dataset ID
  //
  // The returned map will be indexed on dataset ID and will contain values that
  // are themselves maps of project ID to installation status details for that
  // dataset/project id combination
  val datasetInstallStatusMap = AppDB().getDatasetStatuses(projectToDatasetID)
  projectToDatasetID.clear()

  // Get the user details for all the distinct user IDs seen in the dataset
  // listing returned by the original query.
  val userDetails = mutableMapOf<UserID, UserDetails>()
  UserProvider.getUsersById(userIDs.toList().map { it.toLong() })
    .map {
      val userId = UserID(it.key)
      val user = it.value
      val userDetail = UserDetails(userId, user.firstName, user.lastName, user.email, user.organization)
      userDetails.put(userId, userDetail)
    }
  userIDs.clear()

  // Build a list for the results we will be returning.
  val results = ArrayList<DatasetListEntry>(datasetList.size)

  // For each dataset we found with the original query.
  datasetList.forEach {
    // Convert the found dataset into the expected output type
    // (DatasetListEntry) and add it to the result list.
    results.add(it.toListEntry(
      owner = userDetails[it.ownerID] ?: throw IllegalStateException("missing user details for user id ${it.ownerID}"),
      pluginDisplayName = PluginHandlers[it.typeName, it.typeVersion]?.displayName ?: throw IllegalStateException("missing plugin ${it.typeName}:${it.typeVersion}"),
      statuses = datasetInstallStatusMap[it.datasetID] ?: emptyMap(),
      shares = (if (it.ownerID == requesterID) shares[it.datasetID] ?: emptyList() else emptyList())
        .asSequence()
        .filter { it.offerStatus == VDIShareOfferAction.Grant }
        .map { sh ->
          DatasetListShareUser(
            userDetails[sh.recipientID]!!,
            sh.receiptStatus == VDIShareReceiptAction.Accept
          )
        }
        .toList(),
      fileSummary = fileSummaries[it.datasetID] ?: DatasetFileSummary(0u, 0u),
    ))
  }

  return results
}

private fun DatasetRecord.toListEntry(
  owner: UserDetails,
  pluginDisplayName: String,
  statuses: Map<ProjectID, InstallStatuses>,
  shares: List<DatasetListShareUser>,
  fileSummary: DatasetFileSummary,
) = DatasetListEntryImpl().also { out ->
  out.datasetId     = datasetID.toString()
  out.owner         = DatasetOwner(owner)
  out.datasetType   = DatasetTypeInfo(this, pluginDisplayName)
  out.name          = name
  out.summary       = summary
  out.description   = description
  out.projectIds    = projects.toList()
  out.visibility    = DatasetVisibility(visibility)
  out.status        = DatasetStatusInfo(importStatus, statuses)
  out.origin        = origin
  out.sourceUrl     = sourceURL
  if (importStatus !== DatasetImportStatus.Invalid && importStatus !== DatasetImportStatus.Failed) {
    // Don't set shares if import status is failed since dataset will never be usable by others.
    out.shares = shares
  }
  out.fileCount     = fileSummary.count.toInt()
  out.fileSizeTotal = fileSummary.size.toLong()
  out.created       = created.defaultZone()
}
