package vdi.core.reconciler

import vdi.util.io.CloseableIterator
import vdi.core.db.model.ReconcilerTargetRecord

/**
 * Facade over a target database that provides APIs necessary for synchronization reconciliation.
 */
internal interface ReconcilerTarget {

  /**
   * Name of the reconciler target.
   */
  val name: String

  /**
   * Type of the reconciler target.
   */
  val type: ReconcilerTargetType

  /**
   * Generates a stream of all sync control records in the synchronization target sorted by a concatenation of user
   * ID and dataset ID.
   *
   * Note that the returned iterator must be closed to avoid connection leaks.
   */
  fun streamSortedSyncControlRecords(): CloseableIterator<ReconcilerTargetRecord>

  suspend fun deleteDataset(dataset: ReconcilerTargetRecord)
}

