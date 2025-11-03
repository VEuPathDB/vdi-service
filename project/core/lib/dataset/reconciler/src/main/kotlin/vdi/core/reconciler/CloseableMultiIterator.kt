package vdi.core.reconciler

import vdi.model.data.InstallTargetID
import vdi.util.io.CloseableIterator
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.model.ReconcilerTargetRecord

internal class CloseableMultiIterator(private val installTarget: InstallTargetID): CloseableIterator<ReconcilerTargetRecord> {
  private val dataTypeIterator = AppDatabaseRegistry[installTarget]!!.keys().iterator()
  private var current: CloseableIterator<ReconcilerTargetRecord>? = null

  override fun hasNext(): Boolean {
    if (current?.hasNext() == true)
      return true

    while (trySetNext()) {
      if (current!!.hasNext())
        return true
    }

    return false
  }

  override fun next(): ReconcilerTargetRecord =
    if (current == null || !current!!.hasNext()) {
      trySetNext()
      if (current?.hasNext() == true) current!!.next() else throw NoSuchElementException()
    } else {
      current!!.next()
    }

  override fun close() {
    current?.close()
  }

  private fun trySetNext(): Boolean {
    current?.close()
    current = null

    while (dataTypeIterator.hasNext()) {
      current = makeNext()
      if (current != null)
        return true
    }

    return false
  }

  private fun makeNext() =
    AppDB().accessor(installTarget, dataTypeIterator.next())?.streamAllSyncControlRecords()
}
