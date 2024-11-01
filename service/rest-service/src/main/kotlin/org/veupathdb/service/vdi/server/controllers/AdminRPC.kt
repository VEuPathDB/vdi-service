package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.core.StreamingOutput
import kotlinx.coroutines.runBlocking
import org.veupathdb.lib.container.jaxrs.repo.UserRepo
import org.veupathdb.lib.container.jaxrs.server.annotations.AdminRequired
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.generated.resources.Admin
import org.veupathdb.service.vdi.service.admin.getDatasetDetails
import org.veupathdb.service.vdi.service.admin.listAllDatasets
import org.veupathdb.service.vdi.service.admin.listAllS3Objects
import org.veupathdb.service.vdi.service.datasets.createDataset
import org.veupathdb.service.vdi.service.datasets.listBrokenDatasets
import org.veupathdb.service.vdi.util.fixVariableDateString
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.BrokenImportListQuery
import vdi.component.db.cache.model.SortOrder
import vdi.component.install_cleanup.InstallCleaner
import vdi.component.install_cleanup.ReinstallTarget
import vdi.component.pruner.Pruner
import vdi.component.reinstaller.DatasetReinstaller
import vdi.lib.reconciler.Reconciler

// Broken Import Query Constants
private const val biQueryLimitMinimum = 0
private const val biQueryLimitMaximum = 250
private const val biQueryLimitDefault = 100
private const val biQueryOffsetMinimum = 0
private const val biQueryOffsetDefault = 0

@Authenticated(adminOverride = ALLOW_ALWAYS)
@AdminRequired
class AdminRPC : Admin {

  override fun getAdminListBroken(expanded: Boolean?): Admin.GetAdminListBrokenResponse {
    return Admin.GetAdminListBrokenResponse
      .respond200WithApplicationJson(listBrokenDatasets(expanded ?: true))
  }

  override fun postAdminReconciler(): Admin.PostAdminReconcilerResponse {
    return runBlocking {
      if (Reconciler.runFull()) {
        Admin.PostAdminReconcilerResponse.respond204()
      } else {
        Admin.PostAdminReconcilerResponse.respond409()
      }
    }
  }

  override fun postAdminFixBrokenInstalls(
    skipRun: Boolean,
    entity: InstallCleanupRequest?
  ): Admin.PostAdminFixBrokenInstallsResponse {
    if (entity == null)
      throw BadRequestException()

    if (entity.all == true) {
      InstallCleaner.cleanAll()
    } else if (!entity.targets.isNullOrEmpty()) {
      InstallCleaner.cleanTargets(entity.targets.map { ReinstallTarget(DatasetID(it.datasetId), it.projectId) })
    }

    if (!skipRun)
      runBlocking { DatasetReinstaller.tryRun() }

    return Admin.PostAdminFixBrokenInstallsResponse.respond204()
  }

  override fun postAdminDeleteCleanup(): Admin.PostAdminDeleteCleanupResponse {
    Pruner.tryPruneDatasets()

    return Admin.PostAdminDeleteCleanupResponse.respond204()
  }

  override fun getAdminDatasetDetails(datasetId: String?): Admin.GetAdminDatasetDetailsResponse {
    if (datasetId == null) {
      throw BadRequestException("no target dataset ID provided")
    }

    return Admin.GetAdminDatasetDetailsResponse.respond200WithApplicationJson(InternalDatasetDetails(getDatasetDetails(DatasetID(datasetId))))
  }

  override fun postAdminProxyUpload(
    userID: Long?,
    entity: DatasetPostRequest?,
  ): Admin.PostAdminProxyUploadResponse {
    if (userID == null)
      throw BadRequestException("no target user ID provided")

    (entity ?: throw BadRequestException())
      .validate()
      .throwIfNotEmpty()

    val userID = UserID(userID)

    if (UserRepo.Select.registeredUserById(userID.toLong()).isEmpty)
      throw ForbiddenException("target user does not exist or is a guest user")

    val datasetID = DatasetID()

    createDataset(userID, datasetID, entity)

    return Admin.PostAdminProxyUploadResponse
      .respond200WithApplicationJson(DatasetPostResponse(datasetID))
  }

  override fun getAdminFailedImports(
    user: Long?,
    before: String?,
    after: String?,
    limit: Int?,
    offset: Int?,
    sort: String?,
    order: String?,
  ): Admin.GetAdminFailedImportsResponse {
    if (limit != null && (limit < biQueryLimitMinimum || limit > biQueryLimitMaximum))
      throw BadRequestException("invalid limit value")

    if (offset != null && offset < biQueryOffsetMinimum)
      throw BadRequestException("invalid offset value")

    val query = BrokenImportListQuery().also {
      it.userID = user?.toUserID()
      it.before = before?.let { fixVariableDateString(it) { BadRequestException("invalid before date value") } }
      it.after  = after?.let { fixVariableDateString(it) { BadRequestException("invalid after date value") } }
      it.limit  = limit?.toUByte() ?: biQueryLimitDefault.toUByte()
      it.offset = offset?.toUInt() ?: biQueryOffsetDefault.toUInt()
      it.sortBy = sort?.let { BrokenImportListQuery.SortField.fromStringOrNull(it) }
        ?: throw BadRequestException("invalid sort by value")
      it.order  = order?.let { SortOrder.fromStringOrNull(it) }
        ?: throw BadRequestException("invalid sort order value")
    }

    val broken = CacheDB().selectBrokenDatasetImports(query)
      .map(::BrokenImportDetails)

    return Admin.GetAdminFailedImportsResponse
      .respond200WithApplicationJson(BrokenImportListingImpl().also {
        it.meta = BrokenImportListingMetaImpl().also { meta ->
          meta.count = broken.size
          meta.before = before
          meta.after = after
          meta.user = user
          meta.limit = limit
          meta.offset = offset
        }
        it.results = broken
      })
  }

  override fun getAdminListAllDatasets(
    offset: Int?,
    limit: Int?,
    projectId: String?,
    includeDeleted: Boolean?,
  ): Admin.GetAdminListAllDatasetsResponse {
    return Admin.GetAdminListAllDatasetsResponse
      .respond200WithApplicationJson(listAllDatasets(offset, limit, projectId, includeDeleted))
  }

  override fun getAdminListS3Objects(): Admin.GetAdminListS3ObjectsResponse {
    return Admin.GetAdminListS3ObjectsResponse
      .respond200WithTextPlain(StreamingOutput { out -> out.use { listAllS3Objects().transferTo(it) } })
  }
}
