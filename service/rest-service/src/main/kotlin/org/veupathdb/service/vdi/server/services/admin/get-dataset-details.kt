package org.veupathdb.service.vdi.server.services.admin

import org.veupathdb.service.vdi.generated.resources.Admin.GetAdminDatasetDetailsResponse.respond200WithApplicationJson
import org.veupathdb.service.vdi.generated.resources.Admin.GetAdminDatasetDetailsResponse.respond404WithApplicationJson
import org.veupathdb.service.vdi.server.outputs.InternalDatasetDetails
import org.veupathdb.service.vdi.server.outputs.NotFoundError
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.CacheDB

internal fun getDatasetDetails(datasetID: DatasetID) =
  CacheDB().selectAdminDatasetDetails(datasetID)
    ?.let { respond200WithApplicationJson(InternalDatasetDetails(it)) }
    ?: respond404WithApplicationJson(NotFoundError())
