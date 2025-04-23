package vdi.service.server.services.dataset

import org.veupathdb.lib.s3.s34k.objects.StreamObject
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetRecord
import vdi.service.generated.model.DatasetFileListingImpl
import vdi.service.generated.resources.DatasetsVdiIdFiles.GetDatasetsFilesByVdiIdResponse
import vdi.service.s3.DatasetStore
import vdi.service.server.outputs.DatasetZipDetails
import vdi.service.server.outputs.Static404
import vdi.service.server.outputs.wrap
import vdi.service.util.Either
import vdi.service.generated.resources.DatasetsVdiIdFiles.GetDatasetsFilesDataByVdiIdResponse as DataResponse
import vdi.service.generated.resources.DatasetsVdiIdFiles.GetDatasetsFilesUploadByVdiIdResponse as UploadResponse

// region Install-Ready

fun getInstallReadyZipForAdmin(vdiId: DatasetID): Either<StreamObject, DataResponse> =
  CacheDB().selectDataset(vdiId)
    ?.let { DatasetStore.getInstallReadyZip(it.ownerID, vdiId) }
    ?.let { Either.ofLeft(it) }
    ?: Either.ofRight(Static404.wrap())

fun getInstallReadyZipForUser(user: UserID, vdiId: DatasetID): Either<StreamObject, DataResponse> =
  lookupVisibleDataset(user, vdiId)
    ?.let { DatasetStore.getInstallReadyZip(it.ownerID, vdiId) }
    ?.let { Either.ofLeft(it) }
    ?: Either.ofRight(Static404.wrap())

// endregion Install-Ready

// region Raw Upload

fun getUploadFileForAdmin(vdiId: DatasetID): Either<StreamObject, UploadResponse> =
  CacheDB().selectDataset(vdiId)
    ?.let { DatasetStore.getImportReadyZip(it.ownerID, vdiId) }
    ?.let { Either.ofLeft(it) }
    ?: Either.ofRight(Static404.wrap())

fun getUploadFileForUser(user: UserID, vdiId: DatasetID): Either<StreamObject, UploadResponse> =
  lookupVisibleDataset(user, vdiId)
    ?.let { DatasetStore.getImportReadyZip(it.ownerID, vdiId) }
    ?.let { Either.ofLeft(it) }
    ?: Either.ofRight(Static404.wrap())

// endregion Raw Upload

// region List Files

fun listDatasetFilesForAdmin(vdiId: DatasetID) =
  CacheDB().selectDataset(vdiId)
    ?.let { listDatasetFiles(it.ownerID, vdiId) }
    ?.let(GetDatasetsFilesByVdiIdResponse::respond200WithApplicationJson)
    ?: Static404.wrap()

fun listDatasetFilesForUser(user: UserID, vdiId: DatasetID) =
  lookupVisibleDataset(user, vdiId)
    ?.let { listDatasetFiles(it.ownerID, vdiId) }
    ?.let(GetDatasetsFilesByVdiIdResponse::respond200WithApplicationJson)
    ?: Static404.wrap()

private fun listDatasetFiles(owner: UserID, vdiId: DatasetID) =
  DatasetFileListingImpl().apply {
    upload = DatasetZipDetails(DatasetStore.getImportReadyZipSize(owner, vdiId), CacheDB().selectUploadFiles(vdiId))
    install = DatasetStore.getInstallReadyZipSize(owner, vdiId)
      .takeUnless { it < 0 }
      ?.let { DatasetZipDetails(it, CacheDB().selectInstallFiles(vdiId)) }
  }

// endregion List Files

/**
 * Attempts to locate a dataset record that is visible to the target user.
 *
 * A dataset will be visible if any of the following are true:
 * * it is owned by the target user
 * * it has been shared with the target user
 * * it has been marked as public
 */
private fun lookupVisibleDataset(userID: UserID, datasetID: DatasetID): DatasetRecord? =
  CacheDB().selectDatasetForUser(userID, datasetID) // owned or shared
    ?: CacheDB().selectDataset(datasetID)
      ?.takeIf { it.visibility == VDIDatasetVisibility.Public }
