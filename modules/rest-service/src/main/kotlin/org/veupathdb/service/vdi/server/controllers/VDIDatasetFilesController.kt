package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.model.DatasetFileDetailsImpl
import org.veupathdb.service.vdi.generated.model.DatasetFileListingImpl
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsVdIdFiles
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.toDatasetIDOrNull
import org.veupathdb.vdi.lib.common.field.toUserID
import org.veupathdb.vdi.lib.db.cache.CacheDB

@Authenticated(allowGuests = false)
class VDIDatasetFilesController(@Context request: ContainerRequest) : VdiDatasetsVdIdFiles, ControllerBase(request) {

  override fun getVdiDatasetsFilesByVdId(vdId: String): VdiDatasetsVdIdFiles.GetVdiDatasetsFilesByVdIdResponse {
    val datasetID = vdId.toDatasetIDOrNull() ?: throw NotFoundException()
    val userID = userID.toUserID()
    CacheDB.selectDatasetForUser(userID, datasetID) ?: throw NotFoundException()

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesByVdIdResponse.respond200WithApplicationJson(
      DatasetFileListingImpl().apply {
        uploadFiles = DatasetStore.listUploadFiles(userID, datasetID)
          .map { DatasetFileDetailsImpl().apply { name = it.name; size = it.size } }

        dataFiles = DatasetStore.listDataFiles(userID, datasetID)
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
    CacheDB.selectDatasetForUser(userID, datasetID) ?: throw NotFoundException()

    val obj = DatasetStore.getUploadFile(userID, datasetID, fileName) ?: throw NotFoundException()

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse
      .respond200WithApplicationOctetStream { obj.stream.use { inp -> inp.transferTo(it) } }
  }

  override fun getVdiDatasetsFilesDataByVdIdAndFileName(
    vdId: String,
    fileName: String,
  ): VdiDatasetsVdIdFiles.GetVdiDatasetsFilesDataByVdIdAndFileNameResponse {
    val datasetID = vdId.toDatasetIDOrNull() ?: throw NotFoundException()
    val userID = userID.toUserID()
    CacheDB.selectDatasetForUser(userID, datasetID) ?: throw NotFoundException()

    val obj = DatasetStore.getDataFile(userID, datasetID, fileName) ?: throw NotFoundException()

    return VdiDatasetsVdIdFiles.GetVdiDatasetsFilesDataByVdIdAndFileNameResponse
      .respond200WithApplicationOctetStream { obj.stream.use { inp -> inp.transferTo(it) } }
  }
}