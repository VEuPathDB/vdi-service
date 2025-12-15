package vdi.util.events

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vdi.model.EventID
import vdi.model.OriginTimestamp

object EventIDs {
  private val mutex = Mutex(false)
  private var eventID = (System.currentTimeMillis() - OriginTimestamp.toInstant().toEpochMilli()).toULong()

  suspend fun issueID(): EventID =
    mutex.withLock { return eventID++ }
}