package org.veupathdb.service.vdi.service.datasets.list

import org.veupathdb.service.vdi.db.UserDB
import org.veupathdb.service.vdi.generated.model.DatasetImportStatus
import vdi.component.db.cache.CacheDB
import org.veupathdb.service.vdi.generated.model.DatasetListEntry
import org.veupathdb.service.vdi.generated.model.DatasetListEntryImpl
import org.veupathdb.service.vdi.generated.model.DatasetOwnerImpl
import org.veupathdb.service.vdi.generated.model.DatasetStatusInfoImpl
import org.veupathdb.service.vdi.generated.model.DatasetTypeInfoImpl
import org.veupathdb.service.vdi.model.UserDetails
import vdi.component.db.cache.model.DatasetListQuery
import vdi.component.db.cache.model.DatasetRecord
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID
import vdi.component.db.cache.model.DatasetImportStatus as DIS

fun fetchUserDatasetList(query: DatasetListQuery): List<DatasetListEntry> {
  val datasetList = CacheDB.selectDatasetList(query)
  val userIDs     = HashSet<UserID>(datasetList.size)
  val projectToDatasetID = HashMap<String, MutableSet<DatasetID>>()

  datasetList.forEach { ds ->
    userIDs.add(ds.ownerID)
    ds.projects.forEach { project ->
      projectToDatasetID.computeIfAbsent(project) { HashSet(1) }
        .add(ds.datasetID)
    }
  }

  TODO("""
    Lookup dataset statuses by project ID.
    
    We should expect a map of dataset ID to a map of project name to status:
      Map<DatasetID, Map<String, InstallStatus>>
      
    We can then pass the map of project id to status into the toListEntry
    extension function below.
  """)

  val userDetails = UserDB.lookupUserDetails(userIDs)
  userIDs.clear()

  val results = ArrayList<DatasetListEntry>(datasetList.size)

  datasetList.forEach { results.add(it.toListEntry(
    userDetails[it.ownerID] ?: throw IllegalStateException("missing user details for user id ${it.ownerID}")
  )) }

  return results
}

private fun DatasetRecord.toListEntry(owner: UserDetails) = DatasetListEntryImpl().also { out ->
  out.datasetID = datasetID.toString()
  out.owner     = DatasetOwnerImpl().also {
    it.userID = owner.userID.toString().toLong()
    it.firstName = owner.firstName
    it.lastName = owner.lastName
    it.organization = owner.organization
  }
  out.datasetType = DatasetTypeInfoImpl().also {
    it.name = typeName
    it.version = typeVersion
  }
  out.name = name
  out.summary = summary
  out.description = description
  out.projectIDs = projects.toList()
  out.status = DatasetStatusInfoImpl().also {
    it.import = importStatus.toDatasetImportStatus()
    it.install = TODO("Get install status for dataset across all databases??!?")
  }
}

private fun DIS.toDatasetImportStatus(): DatasetImportStatus {
  return when (this) {
    DIS.AwaitingImport -> DatasetImportStatus.AWAITINGIMPORT
    DIS.Importing      -> DatasetImportStatus.IMPORTING
    DIS.Imported       -> DatasetImportStatus.IMPORTED
    DIS.ImportFailed   -> DatasetImportStatus.IMPORTFAILED
  }
}
