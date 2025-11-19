package vdi.service.rest.server.services.admin.report

import vdi.model.meta.DatasetID
import vdi.core.db.cache.CacheDB
import vdi.service.rest.generated.resources.AdminReports.GetAdminReportsDatasetsByVdiIdResponse
import vdi.service.rest.server.outputs.InternalDatasetDetails
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.wrap

internal fun getDatasetDetails(datasetID: DatasetID) =
  CacheDB().selectAdminDatasetDetails(datasetID)
    ?.let(::InternalDatasetDetails)
    ?.let(GetAdminReportsDatasetsByVdiIdResponse::respond200WithApplicationJson)
    ?: Static404.wrap()
