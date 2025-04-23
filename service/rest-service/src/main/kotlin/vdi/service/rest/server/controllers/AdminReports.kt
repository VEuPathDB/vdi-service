package vdi.service.rest.server.controllers

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetSort
import vdi.service.rest.generated.resources.AdminReports
import vdi.service.server.services.admin.*

class AdminReports : vdi.service.rest.generated.resources.AdminReports {
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
    sort: vdi.service.rest.generated.model.AdminReportsImportsFailedGetSort,
    order: vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder,
    limit: Int,
    offset: Int,
  ) = generateFailedImportReport(user, before, after, sort, order, limit, offset)

  override fun getAdminReportsInstallsFailed(expanded: Boolean) =
    generateFailedInstallReport(expanded)

  override fun getAdminReportsObjectStoreListAll() =
    listAllS3Objects()
}
