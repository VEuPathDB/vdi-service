package vdi.service.rest.server.controllers

import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URI
import vdi.model.data.DatasetID
import vdi.service.rest.gen.api.DatasetApi
import vdi.service.rest.gen.model.DatasetPatchRequestBody
import vdi.service.rest.gen.model.DatasetPostMeta
import vdi.service.rest.gen.model.GetDatasetListOwnershipParameter
import vdi.service.rest.gen.model.InstallTarget
import vdi.service.rest.gen.model.support.*
import vdi.service.rest.util.ShortID

class DatasetController : DatasetApi {

  override fun createDataset(
    details: DatasetPostMeta,
    metaFiles: List<File>?,
    dataFiles: List<File>?,
    url: URI?,
  ): UnionCreateDatasetResponse {
    val datasetID = runBlocking { DatasetID(ShortID.generate()) }

    TODO("Not yet implemented")
  }

  override fun reviseDataset(
    datasetId: String,
    details: DatasetPostMeta,
    metaFiles: List<File>?,
    dataFiles: List<File>?,
    url: URI?,
  ): UnionReviseDatasetResponse {
    TODO("Not yet implemented")
  }

  override fun deleteDataset(datasetId: String): UnionDeleteDatasetResponse {
    TODO("Not yet implemented")
  }

  override fun getCommunityDatasetList(installTarget: InstallTarget?): UnionGetCommunityDatasetListResponse {
    TODO("Not yet implemented")
  }

  override fun getDataset(datasetId: String): UnionGetDatasetResponse {
    TODO("Not yet implemented")
  }

  override fun getDatasetList(
    installTarget: InstallTarget?,
    ownership: GetDatasetListOwnershipParameter,
  ): UnionGetDatasetListResponse {
    TODO("Not yet implemented")
  }

  override fun updateDataset(
    datasetId: String,
    datasetPatchRequestBody: DatasetPatchRequestBody?,
  ): UnionUpdateDatasetResponse {
    TODO("Not yet implemented")
  }
}