package org.veupathdb.service.vdi.service.datasets

import org.veupathdb.service.vdi.db.AccountDB
import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.model.InstallStatuses
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetListQuery
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord

fun fetchUserDatasetList(query: DatasetListQuery): List<DatasetListEntry> {
  return fetchDatasetList(CacheDB.selectDatasetList(query))
}

fun fetchCommunityUserDatasetList(): List<DatasetListEntry> {
  return fetchDatasetList(CacheDB.selectNonPrivateDatasets())
}

private fun fetchDatasetList(datasetList: List<DatasetRecord>): List<DatasetListEntry> {

  // build a set for collecting user IDs to use when querying for user details
  val userIDs = HashSet<UserID>(datasetList.size)

  // build a map for collecting project ID -> dataset ID collection mappings
  // to use when querying for dataset status info.
  val projectToDatasetID = HashMap<String, MutableSet<DatasetID>>(12)

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
  val datasetInstallStatusMap = AppDB.getDatasetStatuses(projectToDatasetID)
  projectToDatasetID.clear()

  // Get the user details for all the distinct user IDs seen in the dataset
  // listing returned by the original query.
  val userDetails = AccountDB.lookupUserDetails(userIDs)
  userIDs.clear()

  // Build a list for the results we will be returning.
  val results = ArrayList<DatasetListEntry>(datasetList.size)

  // For each dataset we found with the original query.
  datasetList.forEach {
    // Convert the found dataset into the expected output type
    // (DatasetListEntry) and add it to the result list.
    results.add(it.toListEntry(
      userDetails[it.ownerID] ?: throw IllegalStateException("missing user details for user id ${it.ownerID}"),
      datasetInstallStatusMap[it.datasetID] ?: emptyMap()
    ))
  }

  return results
}

private fun DatasetRecord.toListEntry(
  owner: UserDetails,
  statuses: Map<ProjectID, InstallStatuses>
) = DatasetListEntryImpl().also { out ->
  out.datasetID   = datasetID.toString()
  out.owner       = DatasetOwner(owner)
  out.datasetType = DatasetTypeInfo(this)
  out.name        = name
  out.summary     = summary
  out.description = description
  out.projectIDs  = projects.toList()
  out.visibility  = DatasetVisibility(visibility)
  out.status      = DatasetStatusInfo(importStatus, statuses)
}
