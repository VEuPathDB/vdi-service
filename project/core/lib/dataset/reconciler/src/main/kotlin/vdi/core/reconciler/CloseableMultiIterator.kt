package vdi.core.reconciler

import vdi.model.data.InstallTargetID
import vdi.util.io.CloseableIterator
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.model.ReconcilerTargetRecord

internal class CloseableMultiIterator(private val installTarget: InstallTargetID) : CloseableIterator<ReconcilerTargetRecord> {
  private val dataTypeIterator = AppDatabaseRegistry[installTarget]!!.keys().iterator()
  private var current: CloseableIterator<ReconcilerTargetRecord>? = null

  override fun hasNext() =
    current?.let { it.hasNext() || dataTypeIterator.hasNext() }
      ?: dataTypeIterator.hasNext()

  override fun next(): ReconcilerTargetRecord {
    // iterate until we have a record stream with at least one record in it.
    while (current?.hasNext() != true) {
      // close the previous stream (if one exists)
      current?.close()

      // ensure there is another type to move to for the current project
      if (!dataTypeIterator.hasNext())
        throw NoSuchElementException()

      // attempt to get a stream for the project/type pairing
      current = AppDB().accessor(installTarget, dataTypeIterator.next())?.streamAllSyncControlRecords()
    }

    return current!!.next()
  }

  override fun close() {
    current?.close()
  }
}
