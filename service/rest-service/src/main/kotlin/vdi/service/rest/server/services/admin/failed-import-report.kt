package vdi.service.rest.server.services.admin

import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.BrokenImportListQuery
import vdi.component.db.cache.model.SortOrder
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetSort
import vdi.service.rest.generated.model.BrokenImportListingImpl
import vdi.service.rest.generated.model.BrokenImportListingMetaImpl
import vdi.service.rest.generated.resources.AdminReports
import vdi.service.rest.generated.resources.AdminReports.GetAdminReportsImportsFailedResponse
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
  sort: vdi.service.rest.generated.model.AdminReportsImportsFailedGetSort,
  order: vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder,
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

  return GetAdminReportsImportsFailedResponse.respond200WithApplicationJson(vdi.service.rest.generated.model.BrokenImportListingImpl()
    .also {
    it.meta = vdi.service.rest.generated.model.BrokenImportListingMetaImpl().also { meta ->
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

private fun vdi.service.rest.generated.model.AdminReportsImportsFailedGetSort.toInternal(): BrokenImportListQuery.SortField =
  when (this) {
    vdi.service.rest.generated.model.AdminReportsImportsFailedGetSort.DATE -> BrokenImportListQuery.SortField.Date
  }

private fun vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder.toInternal(): SortOrder =
  when (this) {
    vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder.ASC -> SortOrder.ASCENDING
    vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder.DESC -> SortOrder.DESCENDING
  }
