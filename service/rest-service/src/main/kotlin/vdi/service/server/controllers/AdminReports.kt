package vdi.service.server.controllers

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.service.generated.model.AdminReportsImportsFailedGetOrder
import vdi.service.generated.model.AdminReportsImportsFailedGetSort
import vdi.service.generated.resources.AdminReports
import vdi.service.server.services.admin.*

class AdminReports : AdminReports {
  override fun getAdminReportsDatasetsListAll(
    projectId: String?,
    includeDeleted: Boolean,
    limit: Int,
    offset: Int,
  ) = listAllDatasets(offset, limit, projectId, includeDeleted)

  override fun getAdminReportsDatasetsByVdiId(vdiId: String) =
    getDatasetDetails(DatasetID(vdiId))

  override fun getAdminReportsImportsFailed(
    user: Long?,
    before: String?,
    after: String?,
    sort: AdminReportsImportsFailedGetSort,
    order: AdminReportsImportsFailedGetOrder,
    limit: Int,
    offset: Int,
  ) = generateFailedImportReport(user, before, after, sort, order, limit, offset)

  override fun getAdminReportsInstallsFailed(expanded: Boolean) =
    generateFailedInstallReport(expanded)

  override fun getAdminReportsObjectStoreListAll() =
    listAllS3Objects()
}
