package vdi.service.rest.server.controllers

import jakarta.ws.rs.Consumes
import vdi.model.meta.DatasetID
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetSort
import vdi.service.rest.generated.resources.AdminReports
import vdi.service.rest.server.services.admin.report.*

class AdminReports : AdminReports {
  override fun getAdminReportsDatasetsListAll(
    projectId: String?,
    includeDeleted: Boolean?,
    limit: Int?,
    offset: Int?,
  ) = listAllDatasets(offset!!, limit!!, projectId, includeDeleted!!)

  override fun getAdminReportsDatasetsByVdiId(vdiId: String) =
    getDatasetDetails(DatasetID(vdiId))

  @Consumes("application/json") // hack to get jackson to parse query params
  override fun getAdminReportsImportsFailed(
    user: Long?,
    before: String?,
    after: String?,
    sort: AdminReportsImportsFailedGetSort,
    order: AdminReportsImportsFailedGetOrder,
    limit: Int?,
    offset: Int?,
  ) = generateFailedImportReport(user, before, after, sort, order, limit!!, offset!!)

  override fun getAdminReportsInstallsFailed(expanded: Boolean) =
    generateFailedInstallReport(expanded)

  override fun getAdminReportsObjectStoreListAll() =
    listAllS3Objects()
}
