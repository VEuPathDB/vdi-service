package org.veupathdb.vdi.lib.reconciler

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator

/**
 * Facade over a target database that provides APIs necessary for synchronization reconciliation.
 */
interface ReconcilerTarget {

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
  fun streamSortedSyncControlRecords(): CloseableIterator<VDIReconcilerTargetRecord>

  fun deleteDataset(datasetType: VDIDatasetType, datasetID: DatasetID)
}

