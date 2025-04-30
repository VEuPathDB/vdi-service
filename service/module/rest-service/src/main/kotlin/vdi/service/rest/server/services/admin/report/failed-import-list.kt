package vdi.service.rest.server.services.admin.report

import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.model.BrokenImportListQuery
import vdi.lib.db.cache.model.SortOrder
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetSort
import vdi.service.rest.generated.model.BrokenImportListingImpl
import vdi.service.rest.generated.model.BrokenImportListingMetaImpl
import vdi.service.rest.generated.resources.AdminReports.GetAdminReportsImportsFailedResponse
import vdi.service.rest.server.inputs.toSafeLimit
import vdi.service.rest.server.inputs.toSafeOffset
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.BrokenImportDetails
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.util.fixVariableDateString

private const val DefaultBrokenImportResultLimit = 100u

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

  return GetAdminReportsImportsFailedResponse.respond200WithApplicationJson(BrokenImportListingImpl()
    .also {
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
