package org.veupathdb.service.vdi.service.admin

import jakarta.ws.rs.NotFoundException
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.AdminDatasetDetailsRecord

internal fun getDatasetDetails(datasetID: DatasetID): AdminDatasetDetailsRecord {
    return vdi.component.db.cache.CacheDB().selectAdminDatasetDetails(datasetID = datasetID) ?: throw NotFoundException("No such dataset $datasetID")
}
