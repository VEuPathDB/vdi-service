package vdi.service.server.services.admin

import vdi.service.generated.resources.AdminReports.GetAdminReportsDatasetsByVdiIdResponse
import vdi.service.server.outputs.InternalDatasetDetails
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.CacheDB
import vdi.service.server.outputs.Static404
import vdi.service.server.outputs.wrap

internal fun getDatasetDetails(datasetID: DatasetID) =
  CacheDB().selectAdminDatasetDetails(datasetID)
    ?.let(::InternalDatasetDetails)
    ?.let(GetAdminReportsDatasetsByVdiIdResponse::respond200WithApplicationJson)
    ?: Static404.wrap()
