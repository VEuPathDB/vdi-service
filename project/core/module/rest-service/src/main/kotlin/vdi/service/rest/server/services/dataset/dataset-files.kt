@file:JvmName("DatasetFileService")
package vdi.service.rest.server.services.dataset

import org.veupathdb.lib.s3.s34k.objects.StreamObject
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetRecord
import vdi.model.data.DatasetID
import vdi.model.data.DatasetVisibility
import vdi.model.data.UserID
import vdi.service.rest.generated.model.DatasetFileListingImpl
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles.GetDatasetsFilesByVdiIdResponse
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.outputs.DatasetFileDetails
import vdi.service.rest.server.outputs.DatasetZipDetails
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.util.Either
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles.GetDatasetsFilesDataByVdiIdResponse as DataResponse
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles.GetDatasetsFilesUploadByVdiIdResponse as UploadResponse

// region Install-Ready

fun getInstallReadyZipForAdmin(vdiId: DatasetID): Either<StreamObject, DataResponse> =
  CacheDB().selectDataset(vdiId)
    ?.let { DatasetStore.getInstallReadyZip(it.ownerID, vdiId) }
    ?.let { Either.ofLeft(it) }
    ?: Either.ofRight(Static404.wrap())

fun <T: ControllerBase> T.getInstallReadyZipForUser(vdiId: DatasetID): Either<StreamObject, DataResponse> =
  lookupVisibleDataset(userID, vdiId)
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

fun <T: ControllerBase> T.getUploadFileForUser(vdiId: DatasetID): Either<StreamObject, UploadResponse> =
  lookupVisibleDataset(userID, vdiId)
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

fun <T: ControllerBase> T.listDatasetFilesForUser(vdiId: DatasetID) =
  lookupVisibleDataset(userID, vdiId)
    ?.let { listDatasetFiles(it.ownerID, vdiId) }
    ?.let(GetDatasetsFilesByVdiIdResponse::respond200WithApplicationJson)
    ?: Static404.wrap()

private fun listDatasetFiles(owner: UserID, vdiId: DatasetID) =
  DatasetFileListingImpl().apply {
    upload = DatasetZipDetails(DatasetStore.getImportReadyZipSize(owner, vdiId), CacheDB().selectUploadFiles(vdiId))
    install = DatasetStore.getInstallReadyZipSize(owner, vdiId)
      .takeUnless { it < 0 }
      ?.let { DatasetZipDetails(it, CacheDB().selectInstallFiles(vdiId)) }
    documents = DatasetStore.listDocumentFiles(owner, vdiId)
      .map { DatasetFileDetails(it.baseName, it.size) }
      .takeUnless { it.isEmpty() }
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
fun lookupVisibleDataset(userID: UserID, datasetID: DatasetID): DatasetRecord? =
  CacheDB().selectDatasetForUser(userID, datasetID) // owned or shared
    ?: CacheDB().selectDataset(datasetID)
      ?.takeIf { it.visibility == DatasetVisibility.Public }
