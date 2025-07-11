package vdi.core.reconciler

import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.purgeDataset
import vdi.core.db.cache.withTransaction
import vdi.core.db.model.ReconcilerTargetRecord

internal class CacheDBTarget: ReconcilerTarget {
  override val name = "cache-db"

  override val type = ReconcilerTargetType.Cache

  override fun streamSortedSyncControlRecords() =
    CacheDB().selectAllSyncControlRecords()

  override suspend fun deleteDataset(dataset: ReconcilerTargetRecord) =
    CacheDB().withTransaction { it.purgeDataset(dataset.datasetID, true) }
}
