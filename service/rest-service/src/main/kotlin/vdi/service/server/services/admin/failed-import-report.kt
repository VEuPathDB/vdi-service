package vdi.service.server.services.admin

import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.BrokenImportListQuery
import vdi.component.db.cache.model.SortOrder
import vdi.service.generated.model.AdminReportsImportsFailedGetOrder
import vdi.service.generated.model.AdminReportsImportsFailedGetSort
import vdi.service.generated.model.BrokenImportListingImpl
import vdi.service.generated.model.BrokenImportListingMetaImpl
import vdi.service.generated.resources.AdminReports
import vdi.service.generated.resources.AdminReports.GetAdminReportsImportsFailedResponse
import vdi.service.server.controllers.AdminReports.Constants
import vdi.service.server.inputs.toSafeLimit
import vdi.service.server.inputs.toSafeOffset
import vdi.service.server.outputs.BadRequestError
import vdi.service.server.outputs.BrokenImportDetails
import vdi.service.server.outputs.wrap
import vdi.service.util.fixVariableDateString

private const val DefaultBrokenImportResultLimit = 100

fun generateFailedImportReport(
  user: Long?,
  before: String?,
  after: String?,
  sort: AdminReportsImportsFailedGetSort,
  order: AdminReportsImportsFailedGetOrder,
  limit: Int,
  offset: Int,
): GetAdminReportsImportsFailedResponse {
  val query = BrokenImportListQuery(
    userID = user?.toUserID(),
    before = before?.let { fixVariableDateString(it) ?: return BadRequestError("invalid before date value").wrap() },
    after  = after?.let { fixVariableDateString(it) ?: return BadRequestError("invalid after date value").wrap() },
    limit  = limit.toSafeLimit(DefaultBrokenImportResultLimit),
    offset = offset.toSafeOffset(limit),
    sortBy = sort.toInternal(),
    order  = order.toInternal(),
  )

  val broken = CacheDB().selectBrokenDatasetImports(query)
    .map(::BrokenImportDetails)

  return GetAdminReportsImportsFailedResponse.respond200WithApplicationJson(BrokenImportListingImpl().also {
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

private fun AdminReportsImportsFailedGetSort.toInternal(): BrokenImportListQuery.SortField =
  when (this) {
    AdminReportsImportsFailedGetSort.DATE -> BrokenImportListQuery.SortField.Date
  }

private fun AdminReportsImportsFailedGetOrder.toInternal(): SortOrder =
  when (this) {
    AdminReportsImportsFailedGetOrder.ASC -> SortOrder.ASCENDING
    AdminReportsImportsFailedGetOrder.DESC -> SortOrder.DESCENDING
  }
