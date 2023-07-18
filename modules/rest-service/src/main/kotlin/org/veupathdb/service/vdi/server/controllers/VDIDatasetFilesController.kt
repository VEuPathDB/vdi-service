package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.model.DatasetFileDetailsImpl
import org.veupathdb.service.vdi.generated.model.DatasetFileListingImpl
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsVdIdFiles
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toDatasetIDOrNull
import org.veupathdb.vdi.lib.common.field.toUserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord

@Authenticated(allowGuests = false)
class VDIDatasetFilesController(@Context request: ContainerRequest) : VdiDatasetsVdIdFiles, ControllerBase(request) {

  override fun getVdiDatasetsFilesByVdId(vdId: String): VdiDatasetsVdIdFiles.GetVdiDatasetsFilesByVdIdResponse {
    val datasetID = vdId.toDatasetIDOrNull() ?: throw NotFoundException()
    val userID = userID.toUserID()
    val ds = requireDataset(userID, datasetID)

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesByVdIdResponse.respond200WithApplicationJson(
      DatasetFileListingImpl().apply {
        uploadFiles = DatasetStore.listUploadFiles(ds.ownerID, datasetID)
          .map { DatasetFileDetailsImpl().apply { name = it.name; size = it.size } }

        dataFiles = DatasetStore.listDataFiles(ds.ownerID, datasetID)
          .map { DatasetFileDetailsImpl().apply { name = it.name; size = it.size } }
      }
    )
  }

  override fun getVdiDatasetsFilesUploadByVdIdAndFileName(
    vdId: String,
    fileName: String,
  ): VdiDatasetsVdIdFiles.GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse {
    val datasetID = vdId.toDatasetIDOrNull() ?: throw NotFoundException()
    val userID = userID.toUserID()
    val ds = requireDataset(userID, datasetID)

    val obj = DatasetStore.getUploadFile(ds.ownerID, datasetID, fileName) ?: throw NotFoundException()

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse
      .respond200WithApplicationOctetStream { obj.stream.use { inp -> inp.transferTo(it) } }
  }

  override fun getVdiDatasetsFilesDataByVdIdAndFileName(
    vdId: String,
    fileName: String,
  ): VdiDatasetsVdIdFiles.GetVdiDatasetsFilesDataByVdIdAndFileNameResponse {
    val datasetID = vdId.toDatasetIDOrNull() ?: throw NotFoundException()
    val userID = userID.toUserID()
    val ds = requireDataset(userID, datasetID)

    val obj = DatasetStore.getDataFile(ds.ownerID, datasetID, fileName) ?: throw NotFoundException()

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesDataByVdIdAndFileNameResponse
      .respond200WithApplicationOctetStream { obj.stream.use { inp -> inp.transferTo(it) } }
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
}