package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.generated.model.DatasetFileListingImpl
import org.veupathdb.service.vdi.generated.model.DatasetZipDetails
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord
import vdi.constants.InstallZipName
import vdi.constants.UploadZipName

// region Get Data File
//
// Functions in this group are used for fetching import-processed data files
// from S3.

internal fun getDataFileForAdmin(vdid: DatasetID) =
  with(CacheDB.selectDataset(vdid) ?: throw NotFoundException()) { getDataFile(ownerID, vdid) }

internal fun getDataFileForUser(user: UserID, vdid: DatasetID) =
  with(requireDataset(user, vdid)) { getDataFile(ownerID, vdid) }

private fun getDataFile(owner: UserID, vdid: DatasetID) =
  DatasetStore.getDataFile(owner, vdid) ?: throw NotFoundException()

// endregion Get Data File

// region Get Upload File
//
// Functions in this group are used for fetching raw user upload files from S3.

internal fun getUploadFileForAdmin(vdid: DatasetID) =
  with(CacheDB.selectDataset(vdid) ?: throw NotFoundException()) { getUploadFile(ownerID, vdid) }

internal fun getUploadFileForUser(user: UserID, vdid: DatasetID) =
  with(requireDataset(user, vdid)) { getUploadFile(ownerID, vdid) }

private fun getUploadFile(owner: UserID, vdid: DatasetID) =
  DatasetStore.getUploadFile(owner, vdid) ?: throw NotFoundException()

// endregion Get Upload File

// region List Files
//
// Functions in this group are for listing dataset files in S3.

internal fun listDatasetFilesForAdmin(vdid: DatasetID) =
  with(CacheDB.selectDataset(vdid) ?: throw NotFoundException()) { listDatasetFiles(ownerID, datasetID) }

internal fun listDatasetFilesForUser(user: UserID, vdid: DatasetID) =
  with(requireDataset(user, vdid)) { listDatasetFiles(ownerID, datasetID) }

private fun listDatasetFiles(owner: UserID, vdid: DatasetID) =
  DatasetFileListingImpl().apply {
    upload = DatasetZipDetails(getUploadZipSize(owner, vdid), CacheDB.selectUploadFiles(vdid))

    val tmp = getDataZipSize(owner, vdid)
    if (tmp > -1)
      install = DatasetZipDetails(tmp, CacheDB.selectInstallFiles(vdid))
  }

// endregion List Files

private fun getUploadZipSize(user: UserID, datasetID: DatasetID): Long {
  val listing = DatasetStore.listUploadFiles(user, datasetID)
  if (listing.isEmpty())
    return -1

  for (file in listing)
    if (file.name == UploadZipName)
      return file.size

  return -1
}

private fun getDataZipSize(userID: UserID, datasetID: DatasetID): Long {
  val listing = DatasetStore.listDataFiles(userID, datasetID)
  if (listing.isEmpty())
    return -1

  for (file in listing)
    if (file.name == InstallZipName)
      return file.size

  return -1
}

private fun requireDataset(userID: UserID, datasetID: DatasetID): DatasetRecord {
  var ds = CacheDB.selectDatasetForUser(userID, datasetID)

  if (ds == null) {
    ds = CacheDB.selectDataset(datasetID) ?: throw NotFoundException()

    if (ds.visibility != VDIDatasetVisibility.Public)
      throw ForbiddenException()
  }

  return ds
}
