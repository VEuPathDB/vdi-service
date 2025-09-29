package vdi.core.reconciler

import vdi.model.data.InstallTargetID
import vdi.util.io.CloseableIterator
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.model.ReconcilerTargetRecord

internal class CloseableMultiIterator(private val installTarget: InstallTargetID): CloseableIterator<ReconcilerTargetRecord> {
  private val dataTypeIterator = AppDatabaseRegistry[installTarget]!!.keys().iterator()
  private var current: CloseableIterator<ReconcilerTargetRecord>? = null

  override fun hasNext() =
    current?.let { it.hasNext() || dataTypeIterator.hasNext() }
      ?: dataTypeIterator.hasNext()

  override fun next(): ReconcilerTargetRecord {
    // iterate until we have a record stream with at least one record in it.
    while (current?.hasNext() != true && dataTypeIterator.hasNext()) {
      // close the previous stream (if one exists)
      current?.close()

      if (!dataTypeIterator.hasNext())
        current = AppDB().accessor(installTarget, dataTypeIterator.next())?.streamAllSyncControlRecords()
    }

    return if (current?.hasNext() == true) current!!.next() else throw NoSuchElementException()
  }

  override fun close() {
    current?.close()
  }
}
