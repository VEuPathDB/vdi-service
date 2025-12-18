package vdi.core.reconciler

import vdi.model.meta.InstallTargetID
import vdi.util.io.CloseableIterator
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.model.ReconcilerTargetRecord

internal class CloseableMultiIterator(private val installTarget: InstallTargetID): CloseableIterator<ReconcilerTargetRecord> {
  private val dataTypeIterator = AppDatabaseRegistry[installTarget]!!.keys().iterator()
  private var current: CloseableIterator<ReconcilerTargetRecord>? = null

  override fun hasNext(): Boolean {
    // If our current data type iterator still has records left, return true
    if (current?.hasNext() == true)
      return true

    // Close the current iterator if it exists
    current?.close()
    current = null

    // Attempt to get the next install target to try and make an iterator for
    while (dataTypeIterator.hasNext()) {
      // attempt to make a dataset iterator for the data type
      current = AppDB().accessor(installTarget, dataTypeIterator.next())
        ?.streamAllSyncControlRecords()

      // If we have a new iterator that has records, return true
      if (current?.hasNext() == true)
        return true
    }

    // if we got here then there were no more install targets with datasets left
    // to process.
    return false
  }

  override fun next(): ReconcilerTargetRecord =
    // If we have more records
    current?.takeIf { it.hasNext() }
      // get the next record
      ?.next()
      // or throw an error
      ?: throw NoSuchElementException()

  override fun close() {
    current?.close()
  }
}
