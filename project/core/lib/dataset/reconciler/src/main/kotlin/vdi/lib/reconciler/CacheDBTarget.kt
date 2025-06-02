package vdi.lib.reconciler

import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.purgeDataset
import vdi.lib.db.cache.withTransaction
import vdi.lib.db.model.ReconcilerTargetRecord

internal class CacheDBTarget : ReconcilerTarget {
  override val name = "cache-db"

  override val type = ReconcilerTargetType.Cache

  override fun streamSortedSyncControlRecords() =
    CacheDB().selectAllSyncControlRecords()

  override suspend fun deleteDataset(dataset: ReconcilerTargetRecord) =
    CacheDB().withTransaction { it.purgeDataset(dataset.datasetID, true) }
}
