package org.veupathdb.service.vdi.service.dataset

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.generated.model.DatasetFileListingImpl
import org.veupathdb.service.vdi.genx.model.DatasetZipDetails
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetRecord

// region Get Data File
//
// Functions in this group are used for fetching import-processed data files
// from S3.

internal fun getDataFileForAdmin(vdiId: DatasetID) =
  with(CacheDB().selectDataset(vdiId) ?: throw NotFoundException()) { getDataFile(ownerID, vdiId) }

internal fun getDataFileForUser(user: UserID, vdiId: DatasetID) =
  with(requireDataset(user, vdiId)) { getDataFile(ownerID, vdiId) }

private fun getDataFile(owner: UserID, vdiId: DatasetID) =
  DatasetStore.getInstallReadyZip(owner, vdiId) ?: throw NotFoundException()

// endregion Get Data File

// region Get Upload File
//
// Functions in this group are used for fetching raw user upload files from S3.

internal fun getUploadFileForAdmin(vdiId: DatasetID) =
  with(CacheDB().selectDataset(vdiId) ?: throw NotFoundException()) { getUploadFile(ownerID, vdiId) }

internal fun getUploadFileForUser(user: UserID, vdiId: DatasetID) =
  with(requireDataset(user, vdiId)) { getUploadFile(ownerID, vdiId) }

private fun getUploadFile(owner: UserID, vdiId: DatasetID) =
  DatasetStore.getImportReadyZip(owner, vdiId) ?: throw NotFoundException()

// endregion Get Upload File

// region List Files
//
// Functions in this group are for listing dataset files in S3.

internal fun listDatasetFilesForAdmin(vdiId: DatasetID) =
  with(CacheDB().selectDataset(vdiId) ?: throw NotFoundException()) { listDatasetFiles(ownerID, datasetID) }

internal fun listDatasetFilesForUser(user: UserID, vdiId: DatasetID) =
  with(requireDataset(user, vdiId)) { listDatasetFiles(ownerID, datasetID) }

private fun listDatasetFiles(owner: UserID, vdiId: DatasetID) =
  DatasetFileListingImpl().apply {
    upload = DatasetZipDetails(DatasetStore.getImportReadyZipSize(owner, vdiId), CacheDB().selectUploadFiles(vdiId))

    val tmp = DatasetStore.getInstallReadyZipSize(owner, vdiId)
    if (tmp > -1)
      install = DatasetZipDetails(tmp, CacheDB().selectInstallFiles(vdiId))
  }

// endregion List Files

private fun requireDataset(userID: UserID, datasetID: DatasetID): DatasetRecord {
  var ds = CacheDB().selectDatasetForUser(userID, datasetID)

  if (ds == null) {
    ds = CacheDB().selectDataset(datasetID) ?: throw NotFoundException()

    if (ds.visibility != VDIDatasetVisibility.Public)
      throw ForbiddenException()
  }

  return ds
}
