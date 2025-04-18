package org.veupathdb.service.vdi.server.controllers

import cleanup.InstallCleaner
import cleanup.ReinstallTarget
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.StreamingOutput
import kotlinx.coroutines.runBlocking
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import org.veupathdb.lib.container.jaxrs.server.annotations.AdminRequired
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.generated.resources.Admin
import org.veupathdb.service.vdi.generated.resources.Admin.*
import org.veupathdb.service.vdi.server.inputs.cleanup
import org.veupathdb.service.vdi.server.inputs.validate
import org.veupathdb.service.vdi.server.outputs.*
import org.veupathdb.service.vdi.server.services.admin.*
import org.veupathdb.service.vdi.server.services.dataset.createDataset
import org.veupathdb.service.vdi.server.services.dataset.listInstallFailedDatasets
import org.veupathdb.service.vdi.util.fixVariableDateString
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toDatasetID
import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.BrokenImportListQuery
import vdi.component.db.cache.model.SortOrder
import vdi.component.pruner.Pruner
import vdi.lib.install.retry.DatasetReinstaller
import vdi.lib.logging.logger
import vdi.lib.reconciler.Reconciler

// Broken Import Query Constants
private const val biQueryLimitMinimum = 0
private const val biQueryOffsetMinimum = 0
private const val biQueryOffsetDefault = 0

@AdminRequired
@Authenticated(adminOverride = ALLOW_ALWAYS)
class AdminRPC : Admin {
  override fun getAdminListBroken(expanded: Boolean?): GetAdminListBrokenResponse {
    return GetAdminListBrokenResponse
      .respond200WithApplicationJson(listInstallFailedDatasets(expanded ?: true))
  }

  override fun postAdminReconciler(): PostAdminReconcilerResponse {
    return runBlocking {
      if (Reconciler.runFull()) {
        PostAdminReconcilerResponse.respond204()
      } else {
        PostAdminReconcilerResponse.respond409WithApplicationJson(ConflictErrorImpl().apply {
          message = "reconciler is already running"
        })
      }
    }
  }

  override fun postAdminFixBrokenInstalls(
    skipRun: Boolean,
    entity: InstallCleanupRequestBody?
  ): PostAdminFixBrokenInstallsResponse {
    if (entity == null)
      return PostAdminFixBrokenInstallsResponse.respond400WithApplicationJson(BadRequestError("empty request body"))

    if (entity.all == true) {
      InstallCleaner.cleanAll()
    } else if (!entity.targets.isNullOrEmpty()) {
      InstallCleaner.cleanTargets(entity.targets.map { ReinstallTarget(DatasetID(it.datasetId), it.projectId) })
    }

    if (!skipRun)
      runBlocking { DatasetReinstaller.tryRun() }

    return PostAdminFixBrokenInstallsResponse.respond204()
  }

  override fun postAdminDeleteCleanup(): PostAdminDeleteCleanupResponse {
    Pruner.tryPruneDatasets()

    return PostAdminDeleteCleanupResponse.respond204()
  }

  override fun getAdminDatasetDetails(datasetId: String?): GetAdminDatasetDetailsResponse =
    datasetId?.let(::DatasetID)
      ?.let(::getDatasetDetails)
      ?: GetAdminDatasetDetailsResponse.respond400WithApplicationJson(BadRequestError("no target dataset ID provided"))

  override fun postAdminProxyUpload(userIDRaw: Long?, entity: DatasetPostRequestBody?): PostAdminProxyUploadResponse {
    if (userIDRaw == null)
      return PostAdminProxyUploadResponse.respond400WithApplicationJson(BadRequestError("no target user ID provided"))

    with(entity ?: throw BadRequestException()) {
      cleanup()
      validate().let {
        if (it.isNotEmpty)
          return PostAdminProxyUploadResponse.respond422WithApplicationJson(UnprocessableEntityError(it))
      }
    }

    val userID = UserID(userIDRaw)

    with (UserProvider.getUsersById(listOf(userID.toLong()))) {
      if (isEmpty() || get(userID.toLong())?.isGuest != false)
        return PostAdminProxyUploadResponse.respond403WithApplicationJson(ForbiddenError("target user does not exist or is a guest user"))
    }

    val datasetID = DatasetID()

    createDataset(userID, datasetID, entity)

    return PostAdminProxyUploadResponse
      .respond200WithApplicationJson(DatasetPostResponseBody(datasetID))
  }

  override fun getAdminFailedImports(
    user: Long?,
    before: String?,
    after: String?,
    limit: Int?,
    offset: Int?,
    sort: String?,
    order: String?,
  ): GetAdminFailedImportsResponse {
    if (limit != null && limit < biQueryLimitMinimum)
      return GetAdminFailedImportsResponse.respond400WithApplicationJson(BadRequestError("invalid limit value"))

    if (offset != null && offset < biQueryOffsetMinimum)
      return GetAdminFailedImportsResponse.respond400WithApplicationJson(BadRequestError("invalid offset value"))

    val query = BrokenImportListQuery().also { q ->
      q.userID = user?.toUserID()

      q.before = before?.let {
        fixVariableDateString(it)
          ?: return GetAdminFailedImportsResponse.respond400WithApplicationJson(BadRequestError("invalid before date value"))
      }

      q.after  = after?.let {
        fixVariableDateString(it)
          ?: return GetAdminFailedImportsResponse.respond400WithApplicationJson(BadRequestError("invalid after date value"))
      }

      limit?.toUInt()?.let { lim -> q.limit = lim }

      q.offset = offset?.toUInt() ?: biQueryOffsetDefault.toUInt()

      sort?.let { s -> q.sortBy = BrokenImportListQuery.SortField.fromStringOrNull(s)
        ?: return GetAdminFailedImportsResponse.respond400WithApplicationJson(BadRequestError("invalid sort by value")) }

      order?.let { o -> q.order = SortOrder.fromStringOrNull(o)
        ?: return GetAdminFailedImportsResponse.respond400WithApplicationJson(BadRequestError("invalid sort order value")) }
    }

    val broken = CacheDB().selectBrokenDatasetImports(query)
      .map(::BrokenImportDetails)

    return GetAdminFailedImportsResponse
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

  override fun postAdminPurgeDataset(json: AdminPurgeDatasetPostApplicationJson?): PostAdminPurgeDatasetResponse {
    json.validate()

    val validUserID = json.userId.toUserID()
    val validDatasetID = json.datasetId.toDatasetID()

    logger.info("attempting to purge dataset {}/{}", validUserID, validDatasetID)

    purgeDataset(validUserID, validDatasetID)

    return PostAdminPurgeDatasetResponse.respond204()
  }

  override fun getAdminListAllDatasets(
    offset: Int?,
    limit: Int?,
    projectId: String?,
    includeDeleted: Boolean?,
  ) =
    GetAdminListAllDatasetsResponse
      .respond200WithApplicationJson(listAllDatasets(offset, limit, projectId, includeDeleted))!!

  override fun getAdminListS3Objects() =
    GetAdminListS3ObjectsResponse
      .respond200WithTextPlain(StreamingOutput { out -> out.use { listAllS3Objects().transferTo(it) } })!!
}
