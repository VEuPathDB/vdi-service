package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.ForbiddenException
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.db.UserDB
import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.generated.resources.VdiDatasetsAdmin
import org.veupathdb.service.vdi.service.datasets.createDataset
import org.veupathdb.service.vdi.service.datasets.listBrokenDatasets
import org.veupathdb.service.vdi.util.fixVariableDateString
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserID
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.BrokenImportListQuery
import org.veupathdb.vdi.lib.db.cache.model.SortOrder
import vdi.component.install_cleanup.InstallCleaner
import vdi.component.install_cleanup.ReinstallTarget
import vdi.component.pruner.Pruner
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

// Broken Import Query Constants
private const val biQueryLimitMinimum = 0
private const val biQueryLimitMaximum = 250
private const val biQueryLimitDefault = 100
private const val biQueryOffsetMinimum = 0
private const val biQueryOffsetDefault = 0

class VDIDatasetsAdminController : VdiDatasetsAdmin {

  override fun getVdiDatasetsAdminListBroken(
    expanded: Boolean?,
    authKey: String?,
  ): VdiDatasetsAdmin.GetVdiDatasetsAdminListBrokenResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()

    return VdiDatasetsAdmin.GetVdiDatasetsAdminListBrokenResponse
      .respond200WithApplicationJson(listBrokenDatasets(expanded ?: true))
  }

  override fun postVdiDatasetsAdminInstallCleanup(
    authKey: String?,
    entity: InstallCleanupRequest?,
  ): VdiDatasetsAdmin.PostVdiDatasetsAdminInstallCleanupResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()

    if (entity == null)
      throw BadRequestException()

    if (entity.all == true) {
      InstallCleaner.cleanAll()
    } else if (entity.targets.isNullOrEmpty()) {
      InstallCleaner.cleanTargets(entity.targets.map { ReinstallTarget(DatasetID(it.datasetID), it.projectID) })
    }

    return VdiDatasetsAdmin.PostVdiDatasetsAdminInstallCleanupResponse.respond204()
  }

  override fun postVdiDatasetsAdminDeleteCleanup(
    authKey: String?
  ): VdiDatasetsAdmin.PostVdiDatasetsAdminDeleteCleanupResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()

    Pruner.tryPruneDatasets()

    return VdiDatasetsAdmin.PostVdiDatasetsAdminDeleteCleanupResponse.respond204()
  }

  override fun postVdiDatasetsAdminProxyUpload(
    authKey: String?,
    userID: Long?,
    entity: DatasetPostRequest?,
  ): VdiDatasetsAdmin.PostVdiDatasetsAdminProxyUploadResponse {
    if (authKey != Options.Admin.secretKey)
      throw ForbiddenException()

    if (userID == null)
      throw BadRequestException("no target user ID provided")

    (entity ?: throw BadRequestException())
      .validate()
      .throwIfNotEmpty()

    val userID = UserID(userID)

    if (UserDB.userIsGuest(userID) != false)
      throw ForbiddenException("target user does not exist or is a guest user")

    val datasetID = DatasetID()

    createDataset(userID, datasetID, entity)

    return VdiDatasetsAdmin.PostVdiDatasetsAdminProxyUploadResponse
      .respond200WithApplicationJson(DatasetPostResponse(datasetID))
  }

  override fun getVdiDatasetsAdminFailedImports(
    user: Long?,
    before: String?,
    after: String?,
    limit: Int?,
    offset: Int?,
    sort: String?,
    order: String?,
    adminToken: String?
  ): VdiDatasetsAdmin.GetVdiDatasetsAdminFailedImportsResponse {
    if (adminToken != Options.Admin.secretKey)
      throw ForbiddenException()

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

    val broken = CacheDB.selectBrokenDatasetImports(query)
      .map(::BrokenImportDetails)

    return VdiDatasetsAdmin.GetVdiDatasetsAdminFailedImportsResponse
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
}