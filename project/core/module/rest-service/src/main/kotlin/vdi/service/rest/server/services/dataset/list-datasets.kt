@file:JvmName("DatasetListService")
package vdi.service.rest.server.services.dataset

import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import vdi.core.db.app.AppDB
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.DatasetRecord
import vdi.core.db.cache.query.DatasetListQuery
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetShareOffer
import vdi.model.meta.DatasetShareReceipt
import vdi.model.meta.UserID
import vdi.service.rest.generated.model.DatasetListEntry
import vdi.service.rest.model.UserDetails
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.outputs.DatasetListShareUser
import vdi.service.rest.server.outputs.toExternal
import vdi.service.rest.util.reduceTo

fun <T: ControllerBase> T.fetchUserDatasetList(query: DatasetListQuery): List<DatasetListEntry> {
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
    .filter { it.offerStatus == DatasetShareOffer.Action.Grant }
    .forEach { userIDs.add(it.recipientID) }}

  // build a map for collecting project ID -> dataset ID collection mappings
  // to use when querying for dataset status info.
  val projectToDatasetID = HashMap<String, MutableSet<DatasetID>>(12)

  // Get file counts and sizes for the datasets.
  val fileSummaries = cacheDB.selectUploadFileSummaries(datasetIDs)

  // for each dataset the original search returned
  datasetList
    .asSequence()
    // Get the user ID for the dataset record
    .onEach { userIDs.add(it.ownerID) }
    // Filter down to just datasets that imported successfully
    .filter { it.importStatus == DatasetImportStatus.Complete }
    // Collect datasets to check install statuses for
    .forEach { ds ->
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
  val userDetails = UserProvider.getUsersById(userIDs.map { it.toLong() })
    .asSequence()
    .associate { (userIdLong, user) ->
      val userId = UserID(userIdLong)
      userId to UserDetails(userId, user.firstName, user.lastName, user.email, user.organization)
    }
  userIDs.clear()

  return datasetList.map {
    it.toExternal(
      owner    = userDetails.requireDetails(it.ownerID),
      installs = datasetInstallStatusMap[it.datasetID] ?: emptyMap(),
      fileInfo = fileSummaries[it.datasetID],
      shares   = if (it.ownerID != requesterID)
        null
      else
        shares[it.datasetID]?.asSequence()
          ?.filter { share -> share.offerStatus == DatasetShareOffer.Action.Grant }
          ?.map { sh ->
            DatasetListShareUser(
              userDetails.requireDetails(sh.recipientID),
              sh.receiptStatus == DatasetShareReceipt.Action.Accept
            )
          }
          ?.toList()
    )
  }
}
