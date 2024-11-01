package org.veupathdb.service.vdi.service.admin

import jakarta.ws.rs.NotFoundException
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.CacheDB

internal fun getDatasetDetails(datasetID: DatasetID) =
  CacheDB().selectAdminDatasetDetails(datasetID) ?: throw NotFoundException("No such dataset $datasetID")
