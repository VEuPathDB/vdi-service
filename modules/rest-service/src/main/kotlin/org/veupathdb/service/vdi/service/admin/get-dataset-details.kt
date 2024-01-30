package org.veupathdb.service.vdi.service.admin

import jakarta.ws.rs.NotFoundException
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.AdminDatasetDetailsRecord

internal fun getDatasetDetails(datasetID: DatasetID): AdminDatasetDetailsRecord {
    return CacheDB.selectAdminDatasetDetails(datasetID = datasetID) ?: throw NotFoundException("No such dataset $datasetID")
}
