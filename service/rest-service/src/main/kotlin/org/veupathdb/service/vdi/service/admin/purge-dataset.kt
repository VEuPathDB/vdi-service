package org.veupathdb.service.vdi.service.admin

import jakarta.ws.rs.BadRequestException
import org.veupathdb.service.vdi.generated.model.AdminPurgeDatasetPostApplicationJson
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.withTransaction
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal fun purgeDataset(userID: UserID, datasetID: DatasetID) {
  for (obj in DatasetStore.listObjectsForDataset(userID, datasetID)) {
    obj.delete()
  }

  CacheDB().withTransaction {
    it.deleteInstallFiles(datasetID)
    it.deleteUploadFiles(datasetID)
    it.deleteDatasetMetadata(datasetID)
    it.deleteDatasetProjects(datasetID)
    it.deleteDatasetShareOffers(datasetID)
    it.deleteDatasetShareReceipts(datasetID)
    it.deleteImportControl(datasetID)
    it.deleteImportMessages(datasetID)
    it.deleteSyncControl(datasetID)
    it.deleteDataset(datasetID)
  }
}

@OptIn(ExperimentalContracts::class)
internal fun AdminPurgeDatasetPostApplicationJson?.validate() {
  contract {
    returns() implies (this@validate != null)
  }

  if (this == null || userId == null || datasetId == null)
    throw BadRequestException()
}
